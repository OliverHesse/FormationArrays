package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.events.NodeStateChangeEvent;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.core.formations.manager.ClientDimensionFormationManger;
import net.lucent.formation_arrays.core.formations.manager.DimensionFormationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class FormationEventHandler {

    @SubscribeEvent
    private static void onLevelLoad(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof Level level)) return;
        ClientFormationManagerHolder.remove(level);

    }
    private static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        ClientFormationManagerHolder.clear(); // a bit of extra safety
    }
    @SubscribeEvent
    private static void onNodeTypeChange(NodeTypesChangedEvent event){
        DimensionFormationManager.getFormationManager(event.getLevel()).nodeTypeChanged(event);
    }
    @SubscribeEvent
    private static void onNodeTypeChange(NodeStateChangeEvent.Load event){
        DimensionFormationManager.getFormationManager(event.getLevel()).nodeLoaded(event.getPos());
    }
    @SubscribeEvent
    private static void onNodeTypeChange(NodeStateChangeEvent.Unload event){
        DimensionFormationManager.getFormationManager(event.getLevel()).nodeUnloaded(event.getPos());
    }

    @SubscribeEvent
    private static void onServerTick(ServerTickEvent.Pre event){
        for(ServerLevel level : event.getServer().getAllLevels()) DimensionFormationManager.getFormationManager(level).run();
    }
    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Pre event){
        if(Minecraft.getInstance().isPaused()) return;
        for(ClientDimensionFormationManger manger : ClientFormationManagerHolder.getAllManagers()) manger.tick();
    }
}
