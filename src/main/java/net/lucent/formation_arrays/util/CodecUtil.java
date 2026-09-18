package net.lucent.formation_arrays.util;

import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

public class CodecUtil extends SavedData {


    public static FormationInstance<?,?> loadFormationInstance(CompoundTag tag, RegistryAccess access){
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, access, tag);
        return FormationInstance.load(input.childOrEmpty("instance"),access);

    }
    public static CompoundTag saveFormationInstance(FormationInstance<?,?> formationInstance, RegistryAccess access){
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, access);
        formationInstance.write(output.child("instance"),access);

        return output.buildResult();
    }

}
