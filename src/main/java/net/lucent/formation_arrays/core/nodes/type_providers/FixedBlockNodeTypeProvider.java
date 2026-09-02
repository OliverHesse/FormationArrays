package net.lucent.formation_arrays.core.nodes.type_providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeFactory;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;

public record FixedBlockNodeTypeProvider(List<FormationNodeType> types) implements BlockNodeTypeProvider {

    public static class Factory extends BlockNodeTypeFactory{

        @Override
        public MapCodec<? extends BlockNodeTypeProvider> codec() {
            return RecordCodecBuilder.<FixedBlockNodeTypeProvider>mapCodec(
                    instance->instance.group(
                            Identifier.CODEC.xmap(FormationNodeType::new,FormationNodeType::type).listOf().fieldOf("types").forGetter(FixedBlockNodeTypeProvider::types)
                    ).apply(instance,FixedBlockNodeTypeProvider::new)
            );
        }
    }

    @Override
    public BlockNodeTypeFactory getType() {
        return NodeTypeFactories.FIXED.get();
    }

    @Override
    public Collection<FormationNodeType> getTypes(Level level, BlockState state, BlockPos pos) {

        return types;
    }

    @Override
    public boolean isType(Level level, BlockState state, BlockPos pos, FormationNodeType type) {
        return types.contains(type);
    }
}
