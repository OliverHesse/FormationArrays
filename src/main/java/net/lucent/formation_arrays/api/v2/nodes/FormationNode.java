package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.world.level.Level;

import java.util.Collection;

public interface FormationNode{
    Collection<FormationNodeType> getTypes(); //TODO might be changed to a collection
    boolean isType(FormationNodeType type);
}
