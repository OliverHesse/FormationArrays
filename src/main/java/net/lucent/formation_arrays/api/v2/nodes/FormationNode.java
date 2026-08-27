package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.world.level.Level;

public interface FormationNode{
    FormationNodeType getType(); //TODO might be changed to a collection
    boolean isType(FormationNodeType type);
}
