package net.lucent.formation_arrays.node_handling;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeProvider;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.NodeState;
import net.lucent.formation_arrays.api.v2.nodes.accessor.FormationNodeHolder;
import net.lucent.formation_arrays.api.v2.nodes.accessor.FormationNodeReference;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

    private final Map<BlockPos,Set<FormationNodeType>> cachedTypes = new HashMap<>();
    //if a position is unloaded ALL nodes are unloaded
    private final Set<BlockPos> unloadedPositions = new HashSet<>();



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

    public List<NodeState> getNodeStates(){
        Set<NodeState> uniqueStates = new HashSet<>();
        for(Set<FormationNodeReference> refSet : nodes.values()){
            for(FormationNodeReference ref :refSet){
                for(FormationNodeType type : ref.getNodeTypes(level)) uniqueStates.add(new NodeState(type,ref.getPos()));
            }
        }
        return List.copyOf(uniqueStates);
    }

    /**
     * removes all nodes at a position, mainly used when a chunk is loaded
     * @param pos
     */
    public void clearNodesAt(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        Collection<FormationNodeReference> refs = nodes.get(pos);
        unloadedNodes.removeAll(refs);
        refs.clear();
    }

    public void unloadNodes(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        Set<FormationNodeReference> refSet = nodes.get(pos);
        Set<FormationNodeReference> unloadedNodes = new HashSet<>();
        for(FormationNodeReference ref : refSet){
            for(FormationNodeType type : ref.getNodeTypes(level)){
                unloadedNodes.add(new FormationNodeReference.Unloaded(pos,type));
                System.out.println("node of type "+ type+ " unloaded");
            }
        }
        refSet.clear();;
        refSet.addAll(unloadedNodes);
        this.unloadedNodes.addAll(unloadedNodes);
    }
    /**
     * ensures no nodes are held as unloaded, then adds the new node.
     * the new node can be unloaded
     * @param reference the node reference we want to add
     */
    public void addNode(FormationNodeReference reference){
        if(!isLoaded(reference.getPos())) clearNodesAt(reference.getPos());

        Set<FormationNodeReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

        refSet.add(reference);

        if(reference instanceof FormationNodeReference.Unloaded) unloadedNodes.add(reference);
    }

    protected void createUnloadedStates(List<NodeState> states){
        for(NodeState state : states){
            addNode(new FormationNodeReference.Unloaded(state.pos(),state.type()));
        }
    }
    public void addNode(Entity entity){
        if(entity.getCapability(CoreCapabilities.ENTITY_FORMATION_NODE) == null) return;
        addNode(new FormationNodeReference.Loaded(new FormationNodeHolder.EntityFormationNodeHolder(entity)));
    }
    public void addNode(BlockPos pos){
        System.out.println("trying to add node for "+level.getBlockState(pos).getBlock());
        if(level.getCapability(CoreCapabilities.BLOCK_FORMATION_NODE,pos) == null) return;
        System.out.println("adding node");
        addNode(new FormationNodeReference.Loaded(new FormationNodeHolder.BlockFormationNodeHolder(pos)));
    }
    public void removeNode(Entity entity){
        //TODO
    }
    public void removeNode(BlockPos pos){
        //TODO
    }
    public Collection<BlockPos> getAllNodeLocations(){
        return nodes.keySet();
    }
    //if one is not loaded we can assume they are all not loaded
    public boolean isNodeTypeLoaded(BlockPos pos,FormationNodeType type){
        FormationNodeReference unloaded = new FormationNodeReference.Unloaded(pos,type);
        return !unloadedNodes.contains(unloaded);
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
        for(FormationNodeReference ref : nodes.get(pos)) {
            types.addAll(ref.getNodeTypes(level));
        };
        return types;
    }
    public boolean isLoaded(BlockPos pos){
        if(!nodes.containsKey(pos)) return true;
        for(FormationNodeReference ref : nodes.get(pos)){
            if(ref instanceof FormationNodeReference.Unloaded) return false;
        }
        return true;
    }

    public void triggerTypesChangedEvent(Set<FormationNodeType> newTypes,Set<FormationNodeType> removedTypes,BlockPos pos){

    }

    public void calculateCachedTypes(){

    }
}
