package net.lucent.formation_arrays.api.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class FormationType {

    public abstract MapCodec<? extends Formation> codec();
    public abstract MapCodec<? extends FormationInstance> instanceCodec();

    public static final Codec<Formation> PHYSIQUE_CODEC = CoreRegistries.FORMATION_TYPES.byNameCodec()
            .dispatch(
                    Formation::getType,
                    FormationType::codec
            );


    public abstract void writeInstance(FormationInstance instance, ValueOutput output);
    public abstract FormationInstance loadInstance(ValueInput input);

    //handles the registry access calls, then uses the types load
    public static FormationInstance load(ValueInput input){
        return null;//TODO
    }

    public abstract void encodeInstance(ByteBuf buf);
    public abstract FormationInstance decodeInstance(ByteBuf buf);

    //handles the registry access calls, then uses the type decode
    public static FormationInstance decode(ByteBuf buf){
        return null;//TODO
    }

}
