package net.lucent.formation_arrays.api.v2.nodes.graph;

import net.lucent.formation_arrays.api.v2.nodes.FormationNodeProvider;
import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;

import java.util.Collection;

public interface NodeGraphDefinition {

    /**
     * attempts to superimpose the NodeGraph onto the world if a node of this graph is placed into the world
     * @param sourcePos the position of the node triggering this
     * @param sourceType the type of the node triggering this
     * @param provider the provider to access nodes
     * @return true-> can superimpose. false -> cannot superimpose
     */
    boolean trySuperimpose(BlockPos sourcePos, FormationNodeType sourceType, FormationNodeProvider provider);

    /**
     * @param type the type we want to check
     * @return true->graph contains the node. false -> does not contain the type
     */
    boolean hasType(FormationNodeType type);

    /**
     * @return the set of all unique node types in this graph
     */
    Collection<FormationNodeType> getNodeTypes();
}

