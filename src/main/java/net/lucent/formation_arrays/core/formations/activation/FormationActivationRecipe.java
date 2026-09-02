package net.lucent.formation_arrays.core.formations.activation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.*;

/**
 * A recipe used to determine if a formation can be created
 * @param nodes the nodes and types relative to node centre to create the formation
 * @param canRotate true -> any rotation around y is allowed, false -> no rotations checked
 *
 * TODO get rotation working (for now i just doing fixed)
 */
public record FormationActivationRecipe(List<Node> nodes, boolean canRotate){


    public static final Codec<FormationActivationRecipe> CODEC = RecordCodecBuilder.create(
            instance->instance.group(
                    Node.CODEC.listOf().fieldOf("nodes").forGetter(FormationActivationRecipe::nodes),
                    Codec.BOOL.fieldOf("can_rotate").forGetter(FormationActivationRecipe::canRotate)
            ).apply(instance,FormationActivationRecipe::new)
    );

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
            for(FormationNodeType potentialType : potentinalNode.types()){
                if(!nodeManager.hasNodeType(potentialPos,potentialType)) return false;

            }
        }
        return true;
    }
    public Node getNode(FormationNodeType type){
        for(Node node : nodes){
            if(node.types().contains(type)) return node;
        }
        return null;
    }
    public Node getNode(BlockPos pos){
        for(Node node : nodes){
            if(node.pos().equals(pos)) return node;
        }
        return null;
    }
    public int getIndexOf(BlockPos pos){
        Node node  = getNode(pos);
        return node == null ? -1 : nodes.indexOf(node);
    }
    /**
     * from a given node, returns the global position of each node with the index of the pos -> node in node list
     * @param nodeManager the node manager used
     * @param pos the position of the node
     * @param type the type of the node we are building from
     * @return the global position of each node
     */
    public Set<BlockPos> getGlobalNodePositions(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        Node node = getNode(type);
        if(node == null) return Set.of();
        BlockPos center = pos.subtract(node.pos());
        Set<BlockPos> positions  = new HashSet<>();
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
    public Set<FormationNodeType> getUniqueTypes(){
        Set<FormationNodeType> types = new HashSet<>();
        for(Node node: nodes) types.addAll(node.types());
        return types;
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
