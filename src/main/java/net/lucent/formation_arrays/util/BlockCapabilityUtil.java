package net.lucent.formation_arrays.util;

import net.lucent.formation_arrays.mixin.BlockCapabilityAccessor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nullable;
import java.util.Collection;

public class BlockCapabilityUtil {

    @SuppressWarnings("unchecked")
    public static <T> Collection<Block> getPossibleBlocks(
            BlockCapability<T, Void> capability
    ) {
        return ((BlockCapabilityAccessor<T, Void>) (Object) capability)
                .formation_arrays$getProviders()
                .keySet();
    }
}
