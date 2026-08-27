package net.lucent.formation_arrays;

import net.lucent.formation_arrays.api.v1.nodes.FormationNode;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.lucent.formation_arrays.mixin.BlockCapabilityAccessor;
import net.lucent.formation_arrays.util.BlockCapabilityUtil;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FormationArrays.MOD_ID)
@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class FormationArrays {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "formation_arrays";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public FormationArrays(IEventBus modEventBus, ModContainer modContainer) {



        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public static void onStarting(ServerStartedEvent event){
        Collection<Block> blocks = BlockCapabilityUtil.getPossibleBlocks(CoreCapabilities.BLOCK_FORMATION_NODE);
        System.out.println("nodes");
        for(Block block : blocks) {
            System.out.println(block.getName());
        }

    }

}
