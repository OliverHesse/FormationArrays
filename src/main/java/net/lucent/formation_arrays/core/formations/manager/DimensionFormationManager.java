package net.lucent.formation_arrays.core.formations.manager;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.core.formations.MalformedFormationInstance;
import net.lucent.formation_arrays.core.formations.PlacedFormation;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationHelper;
import net.lucent.formation_arrays.core.nodes.DimensionNodeManager;
import net.lucent.formation_arrays.network.DimensionFormationManagerPatchPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.List;

public class DimensionFormationManager extends SavedData {
    public static DimensionFormationManager getFormationManager(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(STORAGE_ID);
    }
    //TODO set it up so we store raw ValueInput/Output then we can decode it in constructor
    public static final SavedDataType<DimensionFormationManager> STORAGE_ID = new SavedDataType<>(

            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "formations"),
            DimensionFormationManager::new,
            level->
                    RecordCodecBuilder.create(instance -> instance.group(
                            RecordCodecBuilder.point(level),
                            PlacedFormation.codec(level.registryAccess()).listOf().xmap(Set::copyOf,List::copyOf).fieldOf("formations").forGetter(DimensionFormationManager::getPlacedFormations)
                    ).apply(instance, DimensionFormationManager::new))
    );

    public DimensionFormationManager(ServerLevel level) {
        this.level = level;
    }
    public DimensionFormationManager(ServerLevel level,Set<PlacedFormation> formations){
        this.level = level;
        for(PlacedFormation formation : formations){
            if(formation.instance() instanceof MalformedFormationInstance) {

                continue;
            };
            placedFormations.add(formation);
            instanceToPlacement.put(formation.instance(),formation);

            Set<BlockPos> listenedPos = formation.instance().getListenedNodePositions();
            listenedPos.forEach(listenerPos->listeners.computeIfAbsent(listenerPos,key->new HashSet<>()).add(formation.instance()));
            listenedPositions.put(formation.instance(),listenedPos);



        }

    }

    //TODO consider removing formations, since i can just use instanceToPlacements key set


    private final Set<PlacedFormation> placedFormations = new HashSet<>();
    private final Map<FormationInstance,PlacedFormation> instanceToPlacement = new HashMap<>();


    private final Set<PlacedFormation> destroyedFormations = new HashSet<>();

    private final Map<BlockPos,Set<FormationInstance>> listeners = new HashMap<>();
    private final Map<FormationInstance,Set<BlockPos>> listenedPositions = new HashMap<>();

    //network patching
    private final Set<PlacedFormation> dirtyFormations = new HashSet<>();
    private final Set<Long> removedFormations = new HashSet<>();

    private final ServerLevel level;

    private Set<PlacedFormation> getPlacedFormations(){
        return placedFormations;
    }

    public void tryCreateFormation(NodeManager nodeManager, Formation<?,?> formation, BlockPos pos, FormationNodeType type){

        if(!formation.tryActive(nodeManager,pos,type)) return;

        FormationInstance instance = formation.createFormationInstance(nodeManager,pos,type);

        PlacedFormation placedFormation = new PlacedFormation(formation.getRequiredActivationNodes(nodeManager,pos,type),instance);

        //TODO for some reason this check is not working
        if(!placedFormations.add(placedFormation)) return;
        instanceToPlacement.put(instance,placedFormation);

        Set<BlockPos> listenedPos = instance.getListenedNodePositions();

        listenedPos.forEach(listenerPos->listeners.computeIfAbsent(listenerPos,key->new HashSet<>()).add(instance));


        listenedPositions.put(instance,listenedPos);

        setDirty();
    }

    public void scheduleFormationRemoval(FormationInstance instance){
        if(!instanceToPlacement.containsKey(instance)) return;
        destroyedFormations.add(instanceToPlacement.get(instance));
    }

    public void removeFormation(FormationInstance instance){
        PlacedFormation placedFormation = instanceToPlacement.remove(instance);
        if(placedFormation == null) return;
        placedFormations.remove(placedFormation);
        Set<BlockPos> listenedPos = listenedPositions.remove(instance);
        listenedPos.forEach(listenerPos->listeners.computeIfPresent(listenerPos,(key,val)->{
            val.remove(instance);
            return val.isEmpty() ? null : val;
        }));


        instance.destroyed(level,DimensionNodeManager.getNodeManger(level));

        setDirty();
    }
    /**
     * inform all formations listening to this node, then check if any new formations can be created
     * @param event the event holding details about what types where changed for which node
     */
    public void nodeTypeChanged(NodeTypesChangedEvent event){
        Collection<FormationInstance> triggeredListeners = listeners.getOrDefault(event.getPos(),Set.of());
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : triggeredListeners) {
            instance.nodeTypesChanged(level, manager, event.getPos());
            if(!instance.isValid(level,manager)) destroyedFormations.add(instanceToPlacement.get(instance));
        }

        for(FormationNodeType newType : event.getAdded()){
             FormationActivationHelper.getFormations(newType).forEach(formation -> tryCreateFormation(manager,formation,event.getPos(),newType));
        }

        setDirty();
    }
    public void nodeLoaded(BlockPos pos){
        Collection<FormationInstance> triggeredListeners = listeners.getOrDefault(pos,Set.of());
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : triggeredListeners) instance.nodeLoaded(level,manager,pos);
        setDirty();

    }
    public void nodeUnloaded(BlockPos pos){
        Collection<FormationInstance> triggeredListeners = listeners.getOrDefault(pos,Set.of());
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : triggeredListeners) instance.nodeUnloaded(level,manager,pos);

        setDirty();
    }

    /**
     * Run on Level load after node manager is loaded, used to ensure all loaded formations are valid and any other setup
     *
     * TODO also run checks to see if any new formations can be created
     */
    public void init(){
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(PlacedFormation placedFormation : instanceToPlacement.values()){
            if(!placedFormation.instance().isValid(level,manager)) destroyedFormations.add(placedFormation);
        }
    }

    public void run(){
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(PlacedFormation placedFormation : instanceToPlacement.values()){
            if(placedFormation.instance().tick(level,manager)) dirtyFormations.add(placedFormation);
        }

        for(PlacedFormation placedFormation  : destroyedFormations) {
            removeFormation(placedFormation.instance());
            removedFormations.add(placedFormation.id());
        };
        destroyedFormations.clear();

        if(!dirtyFormations.isEmpty() || !removedFormations.isEmpty()) sendPatch();
    }

    public void sendPatch(){

        DimensionFormationManagerPatch patch = new DimensionFormationManagerPatch(dirtyFormations,removedFormations);

        PacketDistributor.sendToPlayersInDimension(level,new DimensionFormationManagerPatchPacket(patch));

        dirtyFormations.clear();
        removedFormations.clear();
    }
}
