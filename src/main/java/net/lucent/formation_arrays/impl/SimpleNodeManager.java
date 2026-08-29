package net.lucent.formation_arrays.impl;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v1.NodeManager;
import net.lucent.formation_arrays.api.v1.nodes.FormationNode;
import net.lucent.formation_arrays.api.v1.nodes.FormationNodeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.*;
@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class SimpleNodeManager implements NodeManager {


    private static SimpleNodeManager nodeManager;


    private record DimensionNode(Identifier dimension,BlockPos pos){}

    private final HashMap<DimensionNode,HashSet<FormationNodeProvider>> dimensionNodes = new HashMap<>();
    private final HashMap<FormationNodeProvider,DimensionNode> formationNodeToWorldPosition = new HashMap<>();




    private final HashMap<Identifier,HashSet<FormationNodeProvider>> placedNodesOfType = new HashMap<>();

    //important when removing, since providers are lazy, we cannot guarantee the implementation will properly handle type changes
    private final HashMap<FormationNodeProvider,Collection<Identifier>> formationNodeTypes = new HashMap<>();
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

        if(!node.isNode()) return;

        System.out.println("trying to add node at position : "+pos);
        System.out.println("side : " + (level.isClientSide() ? "Client":"Server"));
        Collection<Identifier> types = node.getNodeTypes();

        DimensionNode dimensionNode = new DimensionNode(level.dimension().identifier(),pos);
        dimensionNodes.computeIfAbsent(dimensionNode,key->new HashSet<>());
        dimensionNodes.get(dimensionNode).add(nodeProvider);

        formationNodeToWorldPosition.put(nodeProvider,dimensionNode);

        for(Identifier type : types){
            placedNodesOfType.computeIfAbsent(type,key->new HashSet<>());
            placedNodesOfType.get(type).add(nodeProvider);
        }
        formationNodeTypes.put(nodeProvider,types);
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
        DimensionNode dimensionNode = formationNodeToWorldPosition.remove(nodeProvider);
        dimensionNodes.get(dimensionNode).remove(nodeProvider);
        if(dimensionNodes.get(dimensionNode).isEmpty()) dimensionNodes.remove(dimensionNode);

        Collection<Identifier> toRemoveTypes = formationNodeTypes.remove(nodeProvider);

        for(Identifier type : toRemoveTypes){
            placedNodesOfType.get(type).remove(nodeProvider);
            if(placedNodesOfType.get(type).isEmpty()) placedNodesOfType.remove(type);
        }

        //TODO handle formation construction and deconstruction from this

        System.out.println("trying to remove node at position : "+dimensionNode.pos);
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


}
