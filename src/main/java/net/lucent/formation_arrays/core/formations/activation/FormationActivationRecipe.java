package net.lucent.formation_arrays.core.formations.activation;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe used to determine if a formation can be created
 * @param nodes the nodes and types relative to node centre to create the formation
 * @param canRotate true -> any rotation around y is allowed, false -> no rotations checked
 *
 * TODO get rotation working (for now i just doing fixed)
 */
public record FormationActivationRecipe(List<Node> nodes, boolean canRotate){

    /**
     * when an activation node type is detected, tests to see if we can activate the formation
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type of the node we are testing
     * @return true -> create a formation, false -> do not create a formation
     */
    public boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        Node node = getNode(type);
        if(node == null) return false;

        BlockPos center = pos.subtract(node.pos());
        for(Node potentinalNode : nodes){
            BlockPos potentialPos = center.offset(potentinalNode.pos());
            for(FormationNodeType types : node.types()){
                if(!nodeManager.hasNodeType(potentialPos,types)) return false;

            }
        }
        return true;
    }
    private Node getNode(FormationNodeType type){
        for(Node node : nodes){
            if(node.types().contains(type)) return node;
        }
        return null;
    }
    /**
     * from a given node, returns the global position of each node with the index of the pos -> node in node list
     * @param nodeManager the node manager used
     * @param pos the position of the node
     * @param type the type of the node we are building from
     * @return the global position of each node
     */
    public List<BlockPos> getGlobalNodePositions(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        Node node = getNode(type);
        if(node == null) return List.of();
        BlockPos center = pos.subtract(node.pos());
        List<BlockPos> positions  = new ArrayList<>();
        for(Node newNode : nodes) positions.add(center.offset(newNode.pos()));
        return positions;
    }
    public List<Node> getGlobalNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        Node node = getNode(type);
        if(node == null) return List.of();
        BlockPos center = pos.subtract(node.pos());
        List<Node> newNodes  = new ArrayList<>();
        for(Node newNode : nodes) newNodes.add(new Node(center.offset(newNode.pos()),newNode.types()));
        return newNodes;
    }

    /**
     * Given a list of global nodes, get the centre of the formation
     * @param globalNodePositions a list of global node positions
     * @return the centre of the formation
     */
    public BlockPos getCenter(List<BlockPos> globalNodePositions){
        return globalNodePositions.isEmpty() ? BlockPos.ZERO : globalNodePositions.getFirst().subtract(nodes.getFirst().pos());
    }

}
