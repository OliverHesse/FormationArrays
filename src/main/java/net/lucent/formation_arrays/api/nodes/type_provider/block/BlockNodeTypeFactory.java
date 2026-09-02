package net.lucent.formation_arrays.api.nodes.type_provider.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.lucent.formation_arrays.api.CoreRegistries;

public abstract class BlockNodeTypeFactory {
    public abstract MapCodec<? extends BlockNodeTypeProvider> codec();

    public static final Codec<BlockNodeTypeProvider> BLOCK_NODE_TYPE_PROVIDERS = CoreRegistries.BLOCK_NODE_TYPE_FACTORIES.byNameCodec()
            .dispatch(
                    BlockNodeTypeProvider::getType,
                    BlockNodeTypeFactory::codec
            );

}
