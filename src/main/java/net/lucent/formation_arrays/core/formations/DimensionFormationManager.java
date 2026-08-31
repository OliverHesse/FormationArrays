package net.lucent.formation_arrays.core.formations;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.lucent.formation_arrays.api.v2.formations.FormationInstance;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.Node;
import net.lucent.formation_arrays.api.v2.nodes.NodeManager;
import net.lucent.formation_arrays.api.v2.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.core.nodes.DimensionNodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class DimensionFormationManager extends SavedData {
    public static DimensionFormationManager getFormationManager(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(STORAGE_ID);
    }
    //TODO set it up so we store raw ValueInput/Output then we can decode it in constructor
    public static final SavedDataType<DimensionFormationManager> STORAGE_ID = new SavedDataType<>(

            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "formation_nodes"),
            DimensionFormationManager::new,
            level->
                    RecordCodecBuilder.create(instance -> instance.group(
                            RecordCodecBuilder.point(level)
                    ).apply(instance, DimensionFormationManager::new))
    );

    public DimensionFormationManager(ServerLevel level) {
        this.level = level;
    }

    public record PlacedFormation(Set<BlockPos> nodes, FormationInstance instance){

        @Override
        public int hashCode() {
            return Objects.hash(nodes,instance.getFormation().getClass());
        }
    }

    //TODO consider how i simplify this process
    private final Set<PlacedFormation> placedFormations = new HashSet<>();
    private final Map<FormationInstance,PlacedFormation> instanceToPlacement = new HashMap<>();

    private final Set<FormationInstance> formations = new HashSet<>();
    private final Set<FormationInstance> destroyedFormations = new HashSet<>();
    public Map<BlockPos,Set<FormationInstance>> listeners = new HashMap<>();

    private final ServerLevel level;


    public void tryCreateFormation(NodeManager nodeManager, Formation<?,?> formation, BlockPos pos, FormationNodeType type){
        if(!formation.tryActive(nodeManager,pos,type)) return;
        FormationInstance instance = formation.createFormationInstance(nodeManager,pos,type);

        PlacedFormation placedFormation = new PlacedFormation(formation.getRequiredActivationNodes(nodeManager,pos,type),instance);

        if(!placedFormations.add(placedFormation)) return;
        instanceToPlacement.put(instance,placedFormation);

        Collection<BlockPos> listenedPos = instance.getListenedNodePositions();

        listenedPos.forEach(listenerPos->listeners.computeIfAbsent(listenerPos,key->new HashSet<>()).add(instance));
        formations.add(instance);


    }

    public void scheduleFormationRemoval(FormationInstance instance){
        destroyedFormations.add(instance);
    }

    public void removeFormation(FormationInstance instance){
        PlacedFormation placedFormation = instanceToPlacement.remove(instance);
        if(placedFormation == null) return;

        placedFormations.remove(placedFormation);
        Collection<BlockPos> listenedPos = instance.getListenedNodePositions();
        listenedPos.forEach(listenerPos->listeners.computeIfPresent(listenerPos,(key,val)->{
            val.remove(instance);
            return val.isEmpty() ? null : val;
        }));

        formations.remove(instance);

        instance.destroyed(level,DimensionNodeManager.getNodeManger(level));
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

    }
    public void nodeLoaded(BlockPos pos){
        Collection<FormationInstance> triggeredListeners = listeners.getOrDefault(pos,Set.of());
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : triggeredListeners) instance.nodeLoaded(level,manager,pos);

    }
    public void nodeUnloaded(BlockPos pos){
        Collection<FormationInstance> triggeredListeners = listeners.getOrDefault(pos,Set.of());
        NodeManager manager = DimensionNodeManager.getNodeManger(level);
        for(FormationInstance instance : triggeredListeners) instance.nodeUnloaded(level,manager,pos);
    }

    /**
     * Run on Level load after node manager is loaded, used to ensure all loaded formations are valid and any other setup
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
