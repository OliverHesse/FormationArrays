package net.lucent.formation_arrays.core.formations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.core.formations.manager.DimensionFormationManager;
import net.lucent.formation_arrays.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public record PlacedFormation(long id, Set<BlockPos> nodes, FormationInstance instance){

    public static Codec<PlacedFormation> codec(RegistryAccess access){
        return RecordCodecBuilder.create(
                instance->instance.group(
                        BlockPos.CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("nodes").forGetter(PlacedFormation::nodes),
                        CompoundTag.CODEC.xmap(
                                tag -> CodecUtil.loadFormationInstance(tag,access),
                                object -> CodecUtil.saveFormationInstance(object,access)
                        ).fieldOf("formation").forGetter(PlacedFormation::instance)
                ).apply(instance, PlacedFormation::new)
        );
    }
    public PlacedFormation(Set<BlockPos> nodes, FormationInstance instance) {
        this(ThreadLocalRandom.current().nextLong(), nodes, instance);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlacedFormation that = (PlacedFormation) o;
        return Objects.equals(nodes, that.nodes) && Objects.equals(instance.getFormation().getClass(), that.instance.getFormation().getClass());
    }

    @Override
    public int hashCode() {

        return instance.getFormation() == null ? Objects.hash(nodes) :Objects.hash(nodes, instance.getFormation().getClass());
    }
}
