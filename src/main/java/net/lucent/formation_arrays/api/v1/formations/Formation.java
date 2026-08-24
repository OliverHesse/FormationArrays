package net.lucent.formation_arrays.api.v1.formations;

import net.lucent.formation_arrays.api.v1.nodes.NodeGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public interface Formation {


    FormationType getType();
    NodeGraph getGraph();


    //returns the position of the formation from one of its nodes
    BlockPos getPosition(BlockPos pos, Identifier nodeType);

    void tick(Level level, BlockPos pos,FormationInstance instance);
}
