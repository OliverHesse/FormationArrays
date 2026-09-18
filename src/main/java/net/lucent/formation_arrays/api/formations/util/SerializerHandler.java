package net.lucent.formation_arrays.api.formations.util;

import com.mojang.serialization.Codec;
import net.lucent.formation_arrays.core.formations.SimpleStateHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//TODO move over to zenith lib
public interface SerializerHandler<T>{

    T read(ValueInput input,RegistryAccess access);
    void write(T writable, ValueOutput output, RegistryAccess access);


    record CodecSerializerHandler<T>(Codec<T> codec) implements SerializerHandler<T>{

        @Override
        public T read(ValueInput input, RegistryAccess access) {
            return input.read("codec",codec).get();
        }

        @Override
        public void write(T writable, ValueOutput output, RegistryAccess access) {
            output.store("codec",codec,writable);
        }
    }
}
