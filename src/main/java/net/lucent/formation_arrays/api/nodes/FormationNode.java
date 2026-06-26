package net.lucent.formation_arrays.api.nodes;

import java.util.Collection;

/**
 * Anything with this capability Is considered a formation node and can be used to create formations
 */
public interface FormationNode {


    boolean isOfType(NodeType type);

    Collection<NodeType> getNodeTypes();
}
