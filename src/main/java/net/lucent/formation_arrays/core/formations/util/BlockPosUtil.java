package net.lucent.formation_arrays.core.formations.util;

import net.minecraft.core.BlockPos;

public class BlockPosUtil {

    //Rotates a BlockPos around the Y axis in 90 degree increments.
    public static BlockPos rotateAboutY(BlockPos pos, int rotation) {
        return switch (Math.floorMod(rotation, 4)) {
            case 0 -> pos;
            case 1 -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            case 2 -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case 3 -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
            default -> throw new IllegalStateException("Unexpected rotation");
        };
    }

}
