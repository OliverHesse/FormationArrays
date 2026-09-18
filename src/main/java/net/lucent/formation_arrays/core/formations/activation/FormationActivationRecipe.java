package net.lucent.formation_arrays.core.formations.activation;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Set;

public interface FormationActivationRecipe {

    /**
     * when an activation node type is detected, tests to see if we can activate the formation
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type of the node we are testing
     * @return true -> create a formation, false -> do not create a formation
     */
    boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type);

    /**
     * from a given node, returns the global position of each node with the index of the pos -> node in node list
     * @param nodeManager the node manager used
     * @param pos the position of the node
     * @param type the type of the node we are building from
     * @return the global position of each node
     */
    Set<BlockPos> getGlobalNodePositions(NodeManager nodeManager, BlockPos pos, FormationNodeType type);
    List<Node> getGlobalNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type);

    Set<FormationNodeType> getUniqueTypes();

    Node getNode(List<Node> globalPositionedNodes,BlockPos pos);

    BlockPos getCenterFromPositions(List<BlockPos> globalNodePositions);
    BlockPos getCenterFromNodes(List<Node> globalPositionedNodes);
    double getAmplification(List<Node> nodes);
}
