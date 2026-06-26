package net.lucent.formation_arrays.api.formations;

import net.lucent.formation_arrays.api.nodes.NodeGraph;
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
