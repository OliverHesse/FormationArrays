package net.lucent.formation_arrays.api.v1.nodes;

import net.minecraft.resources.Identifier;

import java.util.Collection;

/**
 * Anything with this capability Is considered a formation node and can be used to create formations
 */
public interface FormationNode {


    boolean isOfType(Identifier type);

    Collection<Identifier> getNodeTypes();


    default boolean isNode(){
        return !getNodeTypes().isEmpty();
    }
}
