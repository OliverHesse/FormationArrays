package net.lucent.formation_arrays.impl;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.NodeManager;
import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.api.nodes.FormationNodeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.*;
@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class SimpleNodeManager implements NodeManager {


    private static SimpleNodeManager nodeManager;


    private record DimensionNode(Identifier dimension,BlockPos pos){}

    private final HashMap<DimensionNode,HashSet<FormationNodeProvider>> dimensionNodes = new HashMap<>();
    private final HashMap<FormationNodeProvider,DimensionNode> formationNodeToWorldPosition = new HashMap<>();



    //should include duplicates since multiple providers may try to provide the same node type
    private final HashMap<Identifier,ArrayList<FormationNodeProvider>> placedNodesOfType = new HashMap<>();


    //make sure that when removing nodes, before applying logic for node type removal check if any of that type are still present




    public static SimpleNodeManager getInstance(){
        return nodeManager;
    }

    public boolean hasNode(FormationNodeProvider provider){
        return formationNodeToWorldPosition.containsKey(provider);
    }

    @Override
    public boolean hasNodeType(Level level, BlockPos pos, Identifier type) {
        return false;
    }

    @Override
    public void addNode(Level level, BlockPos pos, FormationNodeProvider nodeProvider) {
        if(hasNode(nodeProvider)) return;


        FormationNode node = nodeProvider.getNode(level);
        if(node == null) return;
        System.out.println("trying to add node at position : "+pos);
        if(node.getNodeTypes().isEmpty()) return;



        //TODO add the node here
    }
    public void removeBlockNodeAt(Level level, BlockPos pos){
        DimensionNode dimensionNode = new DimensionNode(level.dimension().identifier(),pos);
        if(!dimensionNodes.containsKey(dimensionNode)) return;

        FormationNodeProvider toRemove = null;
        for(FormationNodeProvider provider : dimensionNodes.get(dimensionNode)){
            if(provider instanceof FormationNodeProvider.BlockFormationNodeProvider) toRemove = provider;
        }
        if(toRemove != null) removeNode(toRemove);
    }

    @Override
    public void removeNode(FormationNodeProvider nodeProvider) {
        if(!hasNode(nodeProvider)) return;

        System.out.println("trying to remove node at position : "+formationNodeToWorldPosition.get(nodeProvider).pos);
    }

    @Override
    public Collection<Identifier> getNodeTypes(Level level, BlockPos pos) {

        return List.of();
    }

    @Override
    public Collection<FormationNode> getFormationNodes(Level level, BlockPos pos) {
        return List.of();
    }


    @SubscribeEvent
    private static void onServerStarting(ServerAboutToStartEvent event){
        nodeManager = new SimpleNodeManager();

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    private static void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        if(getInstance() == null) return;
        if(event.isCanceled()) return;

    }

    @SubscribeEvent
    private static void onChunkLoad(ChunkEvent.Load event){

    }
    @SubscribeEvent
    private static void onChunkUnload(ChunkEvent.Unload event){

    }
}
