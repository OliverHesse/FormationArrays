package net.lucent.formation_arrays.api;

import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.api.nodes.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Collection;

public interface NodeManager {


    boolean hasNodeType(Level level, BlockPos pos, NodeType type);

    void addNode(Level level,BlockPos pos,FormationNode node);

    void removeNode(FormationNode node);


    Collection<NodeType> getNodeTypes(Level level,BlockPos pos);
    Collection<FormationNode> getFormationNodes(Level level,BlockPos pos);
}
