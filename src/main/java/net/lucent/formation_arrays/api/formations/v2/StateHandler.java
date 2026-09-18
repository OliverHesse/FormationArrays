package net.lucent.formation_arrays.api.formations.v2;

import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface StateHandler {
    boolean isActive(Level level, NodeManager nodeManager);
    boolean isValid(Level level, NodeManager nodeManager);
    Set<BlockPos> getListenedNodePositions();

    BlockPos getFormationPos();

    void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos);
    void nodeLoaded(Level level, NodeManager nodeManager, BlockPos pos);
    void nodeUnloaded(Level level, NodeManager nodeManager, BlockPos pos);


}
