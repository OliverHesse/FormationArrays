package net.lucent.formation_arrays.api.nodes;

import net.lucent.formation_arrays.api.NodeManager;
import net.minecraft.core.BlockPos;

import java.util.Collection;

/**
 * TODO think about renaming to FormationRules or something
 * Used to hold the rules for how Nodes should be positioned
 * Whenever a new node is placed we run through all NodeGraphs and try and transpose them onto the map
 * basically each node graph has an access node, if it is present we can try transpose.
 * get a list of all instances of that access node type and then run a check
 * due to the nature of graphs, and assuming the graph was implemented correctly if this check returns false we can safely ignore the non access nodes (ASSUMPTION)
 */
public interface NodeGraph {


    /**
     * Determines if the NodeGraph is valid for the NodeManager state
     * @param manager the node manager
     * @param nodePosition the position of the accessNode
     * @param type the type of the accessNode
     * @return true->try to make formation false->don't make formation
     */
    boolean isValid(NodeManager manager, BlockPos nodePosition,NodeType type);



    //The list of access nodes we want to try against, done like this because we might have "multi" graph implementations later
    Collection<NodeType> accessNodeType();


}
