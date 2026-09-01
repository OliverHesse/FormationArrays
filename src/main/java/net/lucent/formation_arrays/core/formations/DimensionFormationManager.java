package net.lucent.formation_arrays.core.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationHelper;
import net.lucent.formation_arrays.core.nodes.DimensionNodeManager;
import net.lucent.formation_arrays.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.awt.*;
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
            if(formation.instance instanceof MalformedFormationInstance) {
                System.out.println("discarding formation");
                continue;
            };
            placedFormations.add(formation);
            instanceToPlacement.put(formation.instance,formation);

            Set<BlockPos> listenedPos = formation.instance.getListenedNodePositions();
            listenedPos.forEach(listenerPos->listeners.computeIfAbsent(listenerPos,key->new HashSet<>()).add(formation.instance));
            listenedPositions.put(formation.instance,listenedPos);

            this.formations.add(formation.instance);
            System.out.println("loaded formation");
        }

    }
    public record PlacedFormation(Set<BlockPos> nodes, FormationInstance instance){


        public static Codec<PlacedFormation> codec(RegistryAccess access){
            return RecordCodecBuilder.create(
                    instance->instance.group(
                            BlockPos.CODEC.listOf().xmap(Set::copyOf,List::copyOf).fieldOf("nodes").forGetter(PlacedFormation::nodes),
                            CompoundTag.CODEC.xmap(
                                    tag -> CodecUtil.loadFormationInstance(tag,access),
                                    object -> CodecUtil.saveFormationInstance(object,access)
                            ).fieldOf("formation").forGetter(PlacedFormation::instance)
                    ).apply(instance,PlacedFormation::new)
            );
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            PlacedFormation that = (PlacedFormation) o;
            return Objects.equals(nodes, that.nodes) && Objects.equals(instance.getFormation().getClass(), that.instance.getFormation().getClass());
        }

        @Override
        public int hashCode() {

            return instance.getFormation() == null ? Objects.hash(nodes) :Objects.hash(nodes, instance.getFormation().getClass());
        }
    }

    //TODO consider how i simplify this process
    private final Set<PlacedFormation> placedFormations = new HashSet<>();
    private final Map<FormationInstance,PlacedFormation> instanceToPlacement = new HashMap<>();

    private final Set<FormationInstance> formations = new HashSet<>();
    private final Set<FormationInstance> destroyedFormations = new HashSet<>();

    public Map<BlockPos,Set<FormationInstance>> listeners = new HashMap<>();
    public Map<FormationInstance,Set<BlockPos>> listenedPositions = new HashMap<>();

    private final ServerLevel level;

    private Set<PlacedFormation> getPlacedFormations(){
        return placedFormations;
    }

    public void tryCreateFormation(NodeManager nodeManager, Formation<?,?> formation, BlockPos pos, FormationNodeType type){
        System.out.println("trying to create formation");
        if(!formation.tryActive(nodeManager,pos,type)) return;
        System.out.println("nodes valid");
        FormationInstance instance = formation.createFormationInstance(nodeManager,pos,type);

        PlacedFormation placedFormation = new PlacedFormation(formation.getRequiredActivationNodes(nodeManager,pos,type),instance);

        //TODO for some reason this check is not working
        if(!placedFormations.add(placedFormation)) return;
        instanceToPlacement.put(instance,placedFormation);

        Set<BlockPos> listenedPos = instance.getListenedNodePositions();

        listenedPos.forEach(listenerPos->listeners.computeIfAbsent(listenerPos,key->new HashSet<>()).add(instance));

        formations.add(instance);
        listenedPositions.put(instance,listenedPos);

        setDirty();
    }

    public void scheduleFormationRemoval(FormationInstance instance){
        destroyedFormations.add(instance);
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

        formations.remove(instance);

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
            if(!instance.isValid(level,manager)) destroyedFormations.add(instance);
        }

        for(FormationNodeType newType : event.getAdded()){
            System.out.println("checking formations ("+FormationActivationHelper.getFormations(newType).size()+") for type "+newType.type());
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
        for(FormationInstance instance : formations){
            if(!instance.isValid(level,manager)) destroyedFormations.add(instance);
        }
    }

    public void run(){
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : formations) instance.tick(level,manager);

        for(FormationInstance instance : destroyedFormations) removeFormation(instance);
        destroyedFormations.clear();
    }

}
