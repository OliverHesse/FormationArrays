package net.lucent.formation_arrays.api.v2.nodes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PairCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.v2.CoreRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;

public record NodeState(FormationNodeType type, BlockPos pos) {


    public static final Codec<NodeState> CODEC = RecordCodecBuilder.create(
            instance->instance.group(
                    Identifier.CODEC.xmap(FormationNodeType::new,FormationNodeType::type).fieldOf("type").forGetter(NodeState::type),
                    BlockPos.CODEC.fieldOf("pos").forGetter(NodeState::pos)
            ).apply(instance,NodeState::new)
    );

}
