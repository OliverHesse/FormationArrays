package net.lucent.formation_arrays.api;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeFactory;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = FormationArrays.MOD_ID)
public class CoreDataMaps {


    public static final DataMapType<Block, BlockNodeTypeProvider> BLOCK_NODE_TYPE_PROVIDER = DataMapType.builder(
            Identifier.fromNamespaceAndPath(FormationArrays.MOD_ID, "node_type_providers"),
            // The registry to register the data map for.
            Registries.BLOCK,
            BlockNodeTypeFactory.BLOCK_NODE_TYPE_PROVIDERS
    ).build();


    @SubscribeEvent // on the mod event bus
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(BLOCK_NODE_TYPE_PROVIDER);

    }
}
