package net.lucent.formation_arrays.core;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.core.formations.DimensionFormationManager;
import net.lucent.formation_arrays.core.formations.activation.FormationActivationHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class SetupEventHandler {

    @SubscribeEvent
    private static void onServerAboutToStart(ServerAboutToStartEvent event){
        FormationActivationHelper.init(event.getServer().registryAccess());
    }
    @SubscribeEvent
    private static void onServerStopping(ServerStoppingEvent event){
        FormationActivationHelper.clear();
    }
    @SubscribeEvent
    private static void onLevelLoad(LevelEvent.Load event){
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        DimensionFormationManager.getFormationManager(serverLevel).init();
    }
}
