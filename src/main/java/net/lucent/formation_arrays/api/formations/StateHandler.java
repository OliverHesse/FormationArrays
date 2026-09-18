package net.lucent.formation_arrays.api.formations;

import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface StateHandler {
    boolean isActive(Level level, NodeManager nodeManager);
    boolean isValid(Level level, NodeManager nodeManager);
    Set<BlockPos> getListenedNodePositions();

    /**
     * used for variable sized formations to determine/modify values.
     * @return how much this formation should be amplified by
     */
    default double getAmplification(){return 1;}


    void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos);
    void nodeLoaded(Level level, NodeManager nodeManager, BlockPos pos);
    void nodeUnloaded(Level level, NodeManager nodeManager, BlockPos pos);


}
