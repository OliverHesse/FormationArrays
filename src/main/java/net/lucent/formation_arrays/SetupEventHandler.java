package net.lucent.formation_arrays;

import net.lucent.formation_arrays.core.formations.DimensionFormationManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class SetupEventHandler {

    @SubscribeEvent
    private static void onServerAboutToStart(ServerAboutToStartEvent event){

    }
    @SubscribeEvent
    private static void onLevelLoad(LevelEvent.Load event){
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        DimensionFormationManager.getFormationManager(serverLevel);
    }
}
