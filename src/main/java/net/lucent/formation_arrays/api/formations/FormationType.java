package net.lucent.formation_arrays.api.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.lucent.formation_arrays.api.CoreRegistries;

public abstract class FormationType{
    public abstract MapCodec<? extends Formation<?,?>> definitionCodec();

    public static final Codec<Formation<?,?>> FORMATIONS = CoreRegistries.FORMATION_TYPES.byNameCodec()
            .dispatch(
                    Formation::getType,
                    FormationType::definitionCodec
            );

}
