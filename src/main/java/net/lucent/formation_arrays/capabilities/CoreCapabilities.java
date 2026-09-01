package net.lucent.formation_arrays.capabilities;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.FormationNode;
import net.lucent.formation_arrays.common.util.ModTags;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class CoreCapabilities {
    public static final EntityCapability<FormationNode,Void> ENTITY_FORMATION_NODE =
            EntityCapability.create(
                    Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID,"entity_formation_node"),
                    FormationNode.class,
                    Void.class
            );
    public static final BlockCapability<FormationNode,Void> BLOCK_FORMATION_NODE =
            BlockCapability.create(
                    Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID,"block_formation_node"),
                    FormationNode.class,
                    Void.class
            );
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        /*
        event.registerEntity(
                CoreCapabilities.ENTITY_FORMATION_NODE,
                EntityTypes.PLAYER,
                (player,nul)->new SimpleFormationNode()
        );

         */
       event.registerBlock(
                CoreCapabilities.BLOCK_FORMATION_NODE,
                (level,pos,state,entity,context)->
                    state.is(ModTags.Blocks.FORMATION_NODE) ? new BlockTagFormationNode(level,pos,state,entity) : null
                ,
                Blocks.DRAGON_EGG,
                Blocks.DIAMOND_BLOCK,
                Blocks.GOLD_BLOCK,
               Blocks.IRON_BLOCK
        );

    }

}
