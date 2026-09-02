package net.lucent.formation_arrays.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.core.formations.MalformedFormationInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.filefix.access.SavedDataNbt;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.event.level.BlockEvent;

public class CodecUtil extends SavedData {


    public static FormationInstance loadFormationInstance(CompoundTag tag, RegistryAccess access){
        ValueInput input = TagValueInput.create(
                ProblemReporter.DISCARDING,
                access,
                tag
        );
        Identifier formationId = Identifier.parse(input.getStringOr("formation","none"));
        Registry<Formation<?,?>> formations = CoreRegistries.FORMATIONS.get(access);

        if(!formations.containsKey(formationId)) return  new MalformedFormationInstance();
        Formation<?,?> formation = formations.getValue(formationId);

        if(formation == null) return new MalformedFormationInstance();
        FormationInstance instance = formation.loadFormationInstance(input.childOrEmpty("instance"),access);

        return instance == null ? new MalformedFormationInstance() : instance;
    }
    public static CompoundTag saveFormationInstance(FormationInstance formationInstance, RegistryAccess access){
        TagValueOutput output = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING,
                access
        );

        output.putString("formation", CoreRegistries.FORMATIONS.get(access).getKey(formationInstance.getFormation()).toString());
        formationInstance.write(output.child("instance"),access);

        return output.buildResult();
    }

}
