package net.lucent.formation_arrays.api.formations.util;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.core.formations.SimpleStateHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;

//TODO move over to zenith lib
public interface SyncHandler<T>{
    T decode(ByteBuf buf, RegistryAccess access);
    void encode(T encodable, ByteBuf buf, RegistryAccess access);


    record StreamCodecSyncHandler<T>(StreamCodec<ByteBuf,T> codec) implements SyncHandler<T>{

        @Override
        public T decode(ByteBuf buf, RegistryAccess access) {

            return codec.decode(buf);
        }

        @Override
        public void encode(T encodable, ByteBuf buf, RegistryAccess access) {
            codec.encode(buf,encodable);
        }
    }
}
