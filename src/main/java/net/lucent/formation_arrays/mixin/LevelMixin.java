package net.lucent.formation_arrays.mixin;

import net.lucent.formation_arrays.core.nodes.DimensionNodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
        //TODO rn this is also called when chunks are created, figure out if there is a way to avoid that
        if(cir.getReturnValue() != true) return;

        Level self = (Level) (Object) this;

        if(!(self instanceof ServerLevel serverLevel)) return;

        DimensionNodeManager manager = DimensionNodeManager.getNodeManger(serverLevel);

        manager.updateNodeProvider(pos);
    }
}
