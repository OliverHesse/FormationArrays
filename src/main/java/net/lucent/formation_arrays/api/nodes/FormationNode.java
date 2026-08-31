package net.lucent.formation_arrays.api.nodes;

import java.util.Collection;

public interface FormationNode{
    Collection<FormationNodeType> getTypes(); //TODO might be changed to a collection
    boolean isType(FormationNodeType type);
}
