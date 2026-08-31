package net.lucent.formation_arrays.api.nodes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.List;

public record Node(BlockPos pos, List<FormationNodeType> types) {
    public static final Codec<Node> CODEC = RecordCodecBuilder.create(
            instance->instance.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(Node::pos),
                    Identifier.CODEC.xmap(FormationNodeType::new,FormationNodeType::type).listOf().fieldOf("types").forGetter(Node::types)
            ).apply(instance,Node::new)
    );
}
