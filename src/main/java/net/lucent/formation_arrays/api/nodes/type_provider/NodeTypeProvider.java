package net.lucent.formation_arrays.api.nodes.type_provider;

import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;

public interface NodeTypeProvider {
    Collection<FormationNodeType> getTypes(Level level,BlockPos pos);
    boolean isType(Level level,FormationNodeType type,BlockPos pos);

}
