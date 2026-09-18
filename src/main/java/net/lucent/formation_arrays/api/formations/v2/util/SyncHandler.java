package net.lucent.formation_arrays.api.formations.v2.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//TODO move over to zenith lib
public interface SyncHandler<T>{
    T decode(ByteBuf buf, RegistryAccess access);
    void encode(T encodable, ByteBuf buf, RegistryAccess access);
}
