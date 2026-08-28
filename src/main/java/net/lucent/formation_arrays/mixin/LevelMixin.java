package net.lucent.formation_arrays.mixin;

import net.lucent.formation_arrays.api.v1.nodes.FormationNodeProvider;
import net.lucent.formation_arrays.impl.SimpleNodeManager;
import net.lucent.formation_arrays.node_handling.DimensionNodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//TODO change to LevelChunk setBlockState
@Mixin(Level.class)
public class LevelMixin {


    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onSetBlock(
            BlockPos pos,
            BlockState blockState,
            int updateFlags,
            int updateLimit,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if(cir.getReturnValue() != true) return;

        Level self = (Level) (Object) this;

        if(!(self instanceof ServerLevel serverLevel)) return;

        DimensionNodeManager manager = DimensionNodeManager.getNodeManger(serverLevel);

        manager.updateNode(pos);
    }
}
