package net.lucent.formation_arrays.core.nodes;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.api.nodes.events.NodeStateChangeEvent;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.api.nodes.type_provider.NodeTypeProviderReference;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.lucent.formation_arrays.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;

public class DimensionNodeManager extends SavedData implements NodeManager {

    public static DimensionNodeManager getNodeManger(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(STORAGE_ID);
    }

    public static final SavedDataType<DimensionNodeManager> STORAGE_ID = new SavedDataType<>(

            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "formation_nodes"),
            DimensionNodeManager::new,
            level->
            RecordCodecBuilder.create(instance -> instance.group(
                    RecordCodecBuilder.point(level),
                    Node.CODEC.listOf().fieldOf("nodes").forGetter(DimensionNodeManager::getNodeStates)
            ).apply(instance, DimensionNodeManager::new))
    );

    private final Map<BlockPos,Set<FormationNodeType>> cachedTypes = new HashMap<>();



    private final ServerLevel level;
    private final Map<BlockPos, Set<NodeTypeProviderReference>> nodes = new HashMap<>();

    public DimensionNodeManager(ServerLevel level) {
        this.level = level;
    }

    public DimensionNodeManager(ServerLevel level,List<Node> savedStates) {
        this(level);
        createUnloadedStates(savedStates);
    }

    public List<Node> getNodeStates() {
        List<Node> groupedNodes = new ArrayList<>();
        for(BlockPos pos : nodes.keySet()){
            Set<NodeTypeProviderReference> refSet  = nodes.get(pos);
            Set<FormationNodeType> types = new HashSet<>();
            for(NodeTypeProviderReference ref :refSet){
                types.addAll(ref.getNodeTypes(level));
            }
            groupedNodes.add(new Node(pos,types.stream().toList()));
        }
        return groupedNodes;

    }

    public void triggerLoadStateChange(BlockPos pos){
        NeoForge.EVENT_BUS.post(new NodeStateChangeEvent.Load(level,pos));
    }
    public void triggerUnloadStateChange(BlockPos pos){
        NeoForge.EVENT_BUS.post(new NodeStateChangeEvent.Unload(level,pos));
    }
    /**
     * removes all nodes at a position, mainly used when a chunk is loaded
     * @param pos
     */
    @Override
    public void loadNode(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        nodes.get(pos).clear();
        setDirty();
        triggerLoadStateChange(pos);
    }
    @Override
    public void unloadNode(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        Set<NodeTypeProviderReference> refSet = nodes.get(pos);
        refSet.clear();
        refSet.add(NodeTypeProviderReference.of(pos,List.copyOf(cachedTypes.getOrDefault(pos,Set.of()))));
        triggerUnloadStateChange(pos);
    }
    /**
     * ensures no nodes are held as unloaded, then adds the new node.
     * the new node can be unloaded
     * @param reference the node reference we want to add
     */
    public void addNodeProvider(NodeTypeProviderReference reference){
        if(!isLoaded(reference.getPos())) loadNode(reference.getPos());

        Set<NodeTypeProviderReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

        refSet.add(reference);

        setDirty();


    }
    protected void addUnloadedNode(NodeTypeProviderReference.Unloaded reference){
        Set<NodeTypeProviderReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

        refSet.add(reference);

        setDirty();
    }

    /**
     * creates the initial unloaded states,
     * does not initialize cache since the ticker will handle that and send out proper events
     * @param states
     */
    protected void createUnloadedStates(List<Node> states){

        for(Node state : states){
            addUnloadedNode(NodeTypeProviderReference.of(state.pos(),state.types()));
        }

    }
    @Override
    public void addNodeProvider(Entity entity){
        if(entity.getCapability(CoreCapabilities.ENTITY_FORMATION_NODE) == null) return;
        addNodeProvider(NodeTypeProviderReference.of(entity));
    }
    @Override
    public void addNodeProvider(BlockPos pos){
        if(!BlockUtil.isNodeTypeProvider(level,pos)) return;

        addNodeProvider(NodeTypeProviderReference.of(pos));
    }
    @Override
    public void removeNodeProvider(Entity entity){
        //TODO
    }
    @Override
    public void removeNodeProvider(BlockPos pos){
        NodeTypeProviderReference reference = NodeTypeProviderReference.of(pos);
        nodes.computeIfPresent(pos,(key,val)->{
            val.remove(reference);
            return val.isEmpty() ? null : val;
        });
        setDirty();

    }
    @Override
    public void updateNodeProvider(BlockPos pos){
        //check if node is still a valid node
        if(BlockUtil.isNodeTypeProvider(level,pos)) addNodeProvider(pos);
        else removeNodeProvider(pos);
    }

    @Override
    public void updateNodeProvider(Entity entity) {
        //TODO
    }
    @Override
    public Collection<BlockPos> getAllNodeLocations(){
        return nodes.keySet();
    }

    /*

        return cachedTypes.containsKey(pos) ? cachedTypes.get(pos).contains(type) : false;
     */
    @Override
    public boolean hasNodeType(BlockPos pos, FormationNodeType type) {
        if(!nodes.containsKey(pos)) return false;

        for(NodeTypeProviderReference ref:nodes.get(pos)){
            if(ref.isType(level,type)) return true;
        }
        return false;
    }

    @Override
    public Set<FormationNodeType> getTypes(BlockPos pos) {
        if(!nodes.containsKey(pos)) return Set.of();
        Set<FormationNodeType> types = new HashSet<>();
        for(NodeTypeProviderReference ref : nodes.get(pos)) {
            types.addAll(ref.getNodeTypes(level));
        };
        return types;
    }
    @Override
    public boolean isLoaded(BlockPos pos){
        return level.isLoaded(pos);
    }

    public void triggerTypesChangedEvent(Set<FormationNodeType> newTypes,Set<FormationNodeType> removedTypes,BlockPos pos){
        NeoForge.EVENT_BUS.post(new NodeTypesChangedEvent(level,pos,newTypes,removedTypes));
    }

    public void calculateCachedTypes(){

        Set<BlockPos> cachedPos = new HashSet<>(cachedTypes.keySet());

        for(BlockPos pos : nodes.keySet()){

            cachedPos.remove(pos);

            if(!isLoaded(pos)) continue;

            Set<FormationNodeType> cachedTypeSet = cachedTypes.computeIfAbsent(pos,key->new HashSet<>());
            Set<FormationNodeType> currentTypes = getTypes(pos);
            if(cachedTypeSet.equals(currentTypes)) continue;

            Set<FormationNodeType> removedTypes = new HashSet<>(cachedTypeSet);
            cachedTypeSet.clear();
            Set<FormationNodeType> newTypes = new HashSet<>();

            for(FormationNodeType type : currentTypes){
                if(!removedTypes.contains(type)) newTypes.add(type);
                else removedTypes.remove(type);

                cachedTypeSet.add(type);
            }

            triggerTypesChangedEvent(newTypes,removedTypes,pos);
        }

        for(BlockPos discardedPos : cachedPos){
            Set<FormationNodeType> types = cachedTypes.remove(discardedPos);
            triggerTypesChangedEvent(Set.of(),types,discardedPos);
        }
        setDirty();
    }
}
