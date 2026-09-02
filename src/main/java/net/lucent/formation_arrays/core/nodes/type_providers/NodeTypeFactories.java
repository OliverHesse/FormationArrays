package net.lucent.formation_arrays.core.nodes.type_providers;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeFactory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NodeTypeFactories {
    private static final DeferredRegister<BlockNodeTypeFactory> FACTORIES =
            DeferredRegister.create(CoreRegistries.BLOCK_NODE_TYPE_FACTORIES, FormationArrays.MOD_ID);

    public static final DeferredHolder<BlockNodeTypeFactory,BlockNodeTypeFactory> FIXED = FACTORIES.register(
            "fixed",
            FixedBlockNodeTypeProvider.Factory::new
    );
    public static final DeferredHolder<BlockNodeTypeFactory,BlockNodeTypeFactory> STATEFUL = FACTORIES.register(
            "stateful",
            StateBlockNodeTypeProvider.Factory::new
    );
    public static void register(IEventBus eventBus){

        FACTORIES.register(eventBus);
    }
}
