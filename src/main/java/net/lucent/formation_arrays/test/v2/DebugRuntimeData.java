package net.lucent.formation_arrays.test.v2;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.RuntimeData;
import net.lucent.formation_arrays.api.formations.util.SerializerHandler;
import net.lucent.formation_arrays.api.formations.util.SyncHandler;
import net.lucent.formation_arrays.api.nodes.Node;
import net.lucent.formation_arrays.core.formations.SimpleStateHandler;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class DebugRuntimeData implements RuntimeData {

    public static Codec<DebugRuntimeData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("ticks").forGetter(DebugRuntimeData::getTicks)
            ).apply(instance,DebugRuntimeData::new)
    );
    public static StreamCodec<ByteBuf, DebugRuntimeData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static SerializerHandler<DebugRuntimeData> SERIALIZER = new SerializerHandler.CodecSerializerHandler<>(CODEC);
    public static SyncHandler<DebugRuntimeData> SYNC_HANDLER = new SyncHandler.StreamCodecSyncHandler<>(STREAM_CODEC);


    int ticks = 0;
    public DebugRuntimeData(){

    }
    public DebugRuntimeData(int ticks){this.ticks = ticks;}

    public int getTicks(){return ticks;}
}
