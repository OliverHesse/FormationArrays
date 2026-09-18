package net.lucent.formation_arrays.core.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public record PlacedFormation(long id, FormationInstance<?,?> instance, Set<BlockPos> nodes) {



    public static Codec<PlacedFormation> codec(RegistryAccess access){

        Codec<FormationInstance<?, ?>> formationCodec =
                CompoundTag.CODEC.xmap(
                        tag -> CodecUtil.loadFormationInstance(tag, access),
                        object -> CodecUtil.saveFormationInstance(object, access)
                );
        return RecordCodecBuilder.create(
                instance->instance.group(
                        formationCodec.fieldOf("formation").forGetter(PlacedFormation::instance),
                        BlockPos.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("nodes").forGetter(PlacedFormation::nodes)
                ).apply(instance, PlacedFormation::new)
        );
    }
    public PlacedFormation( FormationInstance<?,?> instance,Set<BlockPos> nodes) {
        this(ThreadLocalRandom.current().nextLong(), instance, nodes);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlacedFormation that = (PlacedFormation) o;
        return Objects.equals(nodes, that.nodes) && Objects.equals(instance.getFormation().getClass(), that.instance.getFormation().getClass());
    }

    @Override
    public int hashCode() {
        return instance.isMalformed() ? Objects.hash(nodes) :Objects.hash(nodes, instance.getFormation().getClass());
    }
}
