package net.lucent.formation_arrays.node_handling;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeProvider;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.NodeState;
import net.lucent.formation_arrays.api.v2.nodes.accessor.FormationNodeReference;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class DimensionNodeManager extends SavedData implements FormationNodeProvider {

    public static DimensionNodeManager getNodeManger(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(STORAGE_ID);
    }

    public static final SavedDataType<DimensionNodeManager> STORAGE_ID = new SavedDataType<>(

            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "formation_nodes"),
            DimensionNodeManager::new,
            level->
            RecordCodecBuilder.create(instance -> instance.group(
                    RecordCodecBuilder.point(level),
                    NodeState.CODEC.listOf().fieldOf("nodes").forGetter(DimensionNodeManager::getNodeStates)
            ).apply(instance, DimensionNodeManager::new))
    );

    private final ServerLevel level;
    private final Map<BlockPos, Set<FormationNodeReference>> nodes = new HashMap<>();

    private final Set<FormationNodeReference> unloadedNodes = new HashSet<>();

    public DimensionNodeManager(ServerLevel level) {
        this.level = level;
    }

    public DimensionNodeManager(ServerLevel level,List<NodeState> savedStates) {
        this(level);
        createUnloadedStates(savedStates);
    }

    //TODO update for when nodes can hold multiple types,in that situation we need to decide if it is treated as 1 node state or multiple (prob multiple)
    public List<NodeState> getNodeStates(){
        Set<NodeState> uniqueStates = new HashSet<>();
        for(Set<FormationNodeReference> refSet : nodes.values()){
            for(FormationNodeReference ref :refSet){
                uniqueStates.add(new NodeState(ref.getNodeType(level),ref.getPos()));
            }
        }
        return List.copyOf(uniqueStates);
    }

    /**
     * Removes a node type at pos from being unloaded, even if multiple of the same type unloaded at the coords,
     *  should be removed as long as one is loaded
     * @param pos the block position where the unloaded node was
     * @param type the last known type of the unloaded node
     */
    public void removeUnloadedNode(BlockPos pos, FormationNodeType type){
        FormationNodeReference.Unloaded unloaded = new FormationNodeReference.Unloaded(pos,type);
        unloadedNodes.remove(unloaded);
        nodes.computeIfPresent(pos,(key,val)->{
            val.remove(unloaded);
            return val;
        });
    }

    /**
     * ensures no nodes are held as unloaded, then adds the new node.
     * the new node can be unloaded
     * @param reference the node reference we want to add
     */
    public void addNode(FormationNodeReference reference){
        removeUnloadedNode(reference.getPos(),reference.getNodeType(level));

        Set<FormationNodeReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

        refSet.add(reference);

        if(reference instanceof FormationNodeReference.Unloaded) unloadedNodes.add(reference);
    }

    protected void createUnloadedStates(List<NodeState> states){
        for(NodeState state : states){
            addNode(new FormationNodeReference.Unloaded(state.pos(),state.type()));
        }
    }

    @Override
    public boolean hasNodeType(Level level, BlockPos pos, FormationNodeType type) {
        if(!nodes.containsKey(pos)) return false;
        for(FormationNodeReference ref:nodes.get(pos)){
            if(ref.isType(type,level)) return true;
        }
        return false;
    }

    @Override
    public Collection<FormationNodeType> getTypes(Level level, BlockPos pos) {
        if(!nodes.containsKey(pos)) return Set.of();
        Set<FormationNodeType> types = new HashSet<>();
        for(FormationNodeReference ref : nodes.get(pos)) types.add(ref.getNodeType(level));
        return types;
    }
}
