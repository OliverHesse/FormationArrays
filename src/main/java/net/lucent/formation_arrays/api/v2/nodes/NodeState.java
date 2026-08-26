package net.lucent.formation_arrays.api.v2.nodes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record NodeState(FormationNodeType type, BlockPos pos) {

}
