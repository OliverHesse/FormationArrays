package net.lucent.formation_arrays.mixin;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = BlockCapability.class)
public interface BlockCapabilityAccessor<T, C> {

    @Accessor("providers")
    Map<Block, List<IBlockCapabilityProvider<T, C>>> formation_arrays$getProviders();


}
