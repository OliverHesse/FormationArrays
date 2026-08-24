package net.lucent.formation_arrays.api.v2.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.v2.CoreRegistries;
import net.lucent.formation_arrays.api.v1.formations.FormationInstance;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class Formation {
    public abstract MapCodec<? extends FormationDefinition> definitionCodec();



    public abstract MapCodec<? extends FormationInstance> instanceCodec();

    public static final Codec<FormationDefinition> FORMATION_DEFINITIONS = CoreRegistries.FORMATIONS.byNameCodec()
            .dispatch(
                    FormationDefinition::getType,
                    Formation::definitionCodec
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
