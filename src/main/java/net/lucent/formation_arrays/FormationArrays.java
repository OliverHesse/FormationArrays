package net.lucent.formation_arrays;

import net.lucent.formation_arrays.core.nodes.type_providers.NodeTypeFactories;
import net.lucent.formation_arrays.test.TestRegistries;
import net.lucent.formation_arrays.util.BlockUtil;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

import java.util.Collection;

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
        TestRegistries.register(modEventBus);
        NodeTypeFactories.register(modEventBus);
    }

    @SubscribeEvent
    public static void onStarting(ServerStartedEvent event){
        Collection<Block> blocks = BlockUtil.getNodeTypeProviders();
        System.out.println("nodes");
        for(Block block : blocks) {
            System.out.println(block.getName());
        }

    }

}
