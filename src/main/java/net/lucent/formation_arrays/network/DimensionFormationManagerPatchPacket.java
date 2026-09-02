package net.lucent.formation_arrays.network;

import io.netty.buffer.Unpooled;
import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.core.formations.ClientFormationManagerHolder;
import net.lucent.formation_arrays.core.formations.manager.DimensionFormationManager;
import net.lucent.formation_arrays.core.formations.manager.DimensionFormationManagerPatch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DimensionFormationManagerPatchPacket(DimensionFormationManagerPatch patch) implements CustomPacketPayload {
    public static final Type<DimensionFormationManagerPatchPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "dimension_formation_manager_patch")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, DimensionFormationManagerPatchPacket> STREAM_CODEC =
            StreamCodec.of((buf,packet)->DimensionFormationManagerPatch.encode(buf,packet.patch),
                    (buf)->new DimensionFormationManagerPatchPacket(DimensionFormationManagerPatch.decode(buf))
            );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static void handle(DimensionFormationManagerPatchPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {

            packet.patch.applyPatch(ClientFormationManagerHolder.getOrCreate(context.player().level()),context.player().registryAccess());
        });
    }
}
