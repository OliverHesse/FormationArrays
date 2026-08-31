package net.lucent.formation_arrays.core.nodes;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.api.nodes.accessor.FormationNodeHolder;
import net.lucent.formation_arrays.api.nodes.accessor.FormationNodeReference;
import net.lucent.formation_arrays.api.nodes.events.NodeStateChangeEvent;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.lucent.formation_arrays.util.BlockCapabilityUtil;
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
    private final Map<BlockPos, Set<FormationNodeReference>> nodes = new HashMap<>();

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
            Set<FormationNodeReference> refSet  = nodes.get(pos);
            Set<FormationNodeType> types = new HashSet<>();
            for(FormationNodeReference ref :refSet){
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
    public void loadNodes(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        nodes.get(pos).clear();
        setDirty();
        triggerLoadStateChange(pos);
    }
    @Override
    public void unloadNodes(BlockPos pos){
        if(!nodes.containsKey(pos)) return;
        Set<FormationNodeReference> refSet = nodes.get(pos);
        refSet.clear();
        refSet.add(new FormationNodeReference.Unloaded(pos,List.copyOf(cachedTypes.getOrDefault(pos,Set.of()))));
        triggerUnloadStateChange(pos);
    }
    /**
     * ensures no nodes are held as unloaded, then adds the new node.
     * the new node can be unloaded
     * @param reference the node reference we want to add
     */
    public void addNode(FormationNodeReference reference){
        if(!isLoaded(reference.getPos())) loadNodes(reference.getPos());

        Set<FormationNodeReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

        refSet.add(reference);

        setDirty();


    }
    protected void addUnloadedNode(FormationNodeReference.Unloaded reference){
        Set<FormationNodeReference> refSet = nodes.computeIfAbsent(reference.getPos(),key->new HashSet<>());

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
            addUnloadedNode(new FormationNodeReference.Unloaded(state.pos(),state.types()));
        }

    }
    @Override
    public void addNode(Entity entity){
        if(entity.getCapability(CoreCapabilities.ENTITY_FORMATION_NODE) == null) return;
        addNode(new FormationNodeReference.Loaded(new FormationNodeHolder.EntityFormationNodeHolder(entity)));
    }
    @Override
    public void addNode(BlockPos pos){
        if(level.getCapability(CoreCapabilities.BLOCK_FORMATION_NODE,pos) == null) return;

        addNode(new FormationNodeReference.Loaded(new FormationNodeHolder.BlockFormationNodeHolder(pos)));
    }
    @Override
    public void removeNode(Entity entity){
        //TODO
    }
    @Override
    public void removeNode(BlockPos pos){
        FormationNodeReference reference = FormationNodeReference.of(pos);
        nodes.computeIfPresent(pos,(key,val)->{
            val.remove(reference);
            return val.isEmpty() ? null : val;
        });
        setDirty();

    }
    @Override
    public void updateNode(BlockPos pos){
        //check if node is still a valid node
        if(BlockCapabilityUtil.canBeNode(level.getBlockState(pos).getBlock())) addNode(pos);
        else removeNode(pos);
    }

    @Override
    public void updateNode(Entity entity) {

    }
    @Override
    public Collection<BlockPos> getAllNodeLocations(){
        return nodes.keySet();
    }

    @Override
    public boolean hasNodeType(BlockPos pos, FormationNodeType type) {
        if(!nodes.containsKey(pos)) return false;
        for(FormationNodeReference ref:nodes.get(pos)){
            if(ref.isType(type,level)) return true;
        }
        return false;
    }

    @Override
    public Set<FormationNodeType> getTypes(BlockPos pos) {
        if(!nodes.containsKey(pos)) return Set.of();
        Set<FormationNodeType> types = new HashSet<>();
        for(FormationNodeReference ref : nodes.get(pos)) {
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
            System.out.println("updated types for pos "+ pos);
            triggerTypesChangedEvent(newTypes,removedTypes,pos);
        }

        for(BlockPos discardedPos : cachedPos){
            Set<FormationNodeType> types = cachedTypes.remove(discardedPos);
            triggerTypesChangedEvent(Set.of(),types,discardedPos);
        }
        setDirty();
    }
}
