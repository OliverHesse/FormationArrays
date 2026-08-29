package net.lucent.formation_arrays.api.v2.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.v2.CoreRegistries;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class FormationType{
    public abstract MapCodec<Formation<?,?>> definitionCodec();

    public static final Codec<Formation<?,?>> FORMATIONS = CoreRegistries.FORMATION_TYPES.byNameCodec()
            .dispatch(
                    Formation::getType,
                    FormationType::definitionCodec
            );

}
