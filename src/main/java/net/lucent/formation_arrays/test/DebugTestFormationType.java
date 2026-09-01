package net.lucent.formation_arrays.test;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationRecipe;
import net.minecraft.core.BlockPos;

public class DebugTestFormationType extends FormationType {
    @Override
    public MapCodec<? extends Formation<?, ?>> definitionCodec() {
        return RecordCodecBuilder.<DebugTestFormation>mapCodec(instance->instance.group(
                FormationActivationRecipe.CODEC.fieldOf("activation_nodes").forGetter(DebugTestFormation::recipe),
                BlockPos.CODEC.optionalFieldOf("control_node").forGetter(DebugTestFormation::controlNode)
            ).apply(instance,DebugTestFormation::new)
        );
    }
}
