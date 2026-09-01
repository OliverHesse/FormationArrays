package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.events.NodeStateChangeEvent;
import net.lucent.formation_arrays.api.nodes.events.NodeTypesChangedEvent;
import net.lucent.formation_arrays.core.nodes.DimensionNodeManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class FormationEventHandler {


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
}
