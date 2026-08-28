package net.lucent.formation_arrays.node_handling;

import net.lucent.formation_arrays.FormationArrays;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class NodeManagerEventHandler {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event){
        event.getServer().getAllLevels().forEach(NodeManagerEventHandler::processServer);
    }

    public static void processServer(ServerLevel level){

    }
}
