package net.lucent.formation_arrays.core.nodes.type_providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.nodes.FormationNodeType;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeFactory;
import net.lucent.formation_arrays.api.nodes.type_provider.block.BlockNodeTypeProvider;
import net.lucent.formation_arrays.capabilities.CoreCapabilities;
import net.lucent.formation_arrays.capabilities.StatefulBlock;
import net.lucent.formation_arrays.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record StateBlockNodeTypeProvider(Map<Integer, List<FormationNodeType>> types) implements BlockNodeTypeProvider {


    public static class Factory extends BlockNodeTypeFactory{

        @Override
        public MapCodec<? extends BlockNodeTypeProvider> codec() {
            return RecordCodecBuilder.<StateBlockNodeTypeProvider>mapCodec(
                    instance->instance.group(
                                    Codec.unboundedMap(
                                            Codec.STRING.xmap(Integer::valueOf,String::valueOf),
                                            Identifier.CODEC.xmap(FormationNodeType::new,FormationNodeType::type).listOf()
                                    ).fieldOf("state_types").forGetter(StateBlockNodeTypeProvider::types)
                           ).apply(instance,StateBlockNodeTypeProvider::new)
            );
        }
    }

    @Override
    public BlockNodeTypeFactory getType() {
        return NodeTypeFactories.STATEFUL.get();
    }

    @Override
    public Collection<FormationNodeType> getTypes(Level level, BlockState state, BlockPos pos) {
        StatefulBlock statefulBlock = BlockUtil.getCapabilityIfLoaded(CoreCapabilities.STATEFUL_BLOCK,level,pos,state);
        if(statefulBlock == null) return types.getOrDefault(0,List.of());

        return types.getOrDefault(statefulBlock.getState(),List.of());
    }

    @Override
    public boolean isType(Level level, BlockState state, BlockPos pos, FormationNodeType type) {
        return getTypes(level,state,pos).contains(type);
    }
}
