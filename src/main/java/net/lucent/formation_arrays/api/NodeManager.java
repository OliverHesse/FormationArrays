package net.lucent.formation_arrays.api;

import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.api.nodes.FormationNodeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.Collection;

public interface NodeManager {


    boolean hasNodeType(Level level, BlockPos pos, Identifier type);

    void addNode(Level level, BlockPos pos, FormationNodeProvider nodeProvider);

    void removeNode(FormationNodeProvider nodeProvider);


    Collection<Identifier> getNodeTypes(Level level, BlockPos pos);
    Collection<FormationNode> getFormationNodes(Level level,BlockPos pos);
}
