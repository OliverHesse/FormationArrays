package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.v2.nodes.Node;
import net.lucent.formation_arrays.api.v2.nodes.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * A recipe used to determine if a formation can be created
 * @param nodes the nodes and types relative to node center to create the formation
 * @param canRotate true -> any rotation around y is allowed, false -> no rotations checked
 */
public record FormationActivationRecipe(List<Node> nodes, boolean canRotate){

    /**
     * when an activation node type is detected, tests to see if we can activate the formation
     * @param nodeManager the mangar handling nodes
     * @param pos the position of the node
     * @param type the type we detected being added
     * @return true -> create a formation, false -> do not create a formation
     */
    public boolean tryActive(NodeManager nodeManager, BlockPos pos, FormationNodeType type){
        //TODO
        return false;
    }

    /**
     * from a given node, returns the global position of each node with the index of the pos -> node in node list
     * @return the global position of each node
     */
    public List<BlockPos> getGlobalNodes(NodeManager nodeManager, BlockPos pos, FormationNodeType type){

        return List.of();
    }

    /**
     * Given a list of global nodes, get the center of the formation
     * @param globalNodePositions a list of global node positions
     * @return the center of the formation
     */
    public BlockPos getCenter(List<BlockPos> globalNodePositions){
        return BlockPos.ZERO;
    }

}
