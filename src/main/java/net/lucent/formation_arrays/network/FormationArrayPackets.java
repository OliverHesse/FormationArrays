package net.lucent.formation_arrays.network;

import net.lucent.formation_arrays.FormationArrays;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class FormationArrayPackets {


    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar(FormationArrays.MOD_ID);

        registrar.playToClient(
                DimensionFormationManagerPatchPacket.TYPE,
                DimensionFormationManagerPatchPacket.STREAM_CODEC
        );
    }

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                DimensionFormationManagerPatchPacket.TYPE,
                DimensionFormationManagerPatchPacket::handle
        );
    }
}
