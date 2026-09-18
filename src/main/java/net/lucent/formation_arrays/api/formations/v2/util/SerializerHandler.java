package net.lucent.formation_arrays.api.formations.v2.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//TODO move over to zenith lib
public interface SerializerHandler<T>{

    T read(ValueInput input,RegistryAccess access);
    void write(T writable, ValueOutput output, RegistryAccess access);
}
