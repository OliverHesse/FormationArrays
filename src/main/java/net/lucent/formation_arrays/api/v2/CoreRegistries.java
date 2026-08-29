package net.lucent.formation_arrays.api.v2;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v2.formations.FormationType;
import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.minecraft.core.Registry;
import net.zic.zenithlib.registry.RegistryHelper;

public class CoreRegistries {

    public static final Registry<FormationType> FORMATION_TYPES = RegistryHelper.registry(FormationArrays.MOD_ID,"formations");

    public static final RegistryHelper.DataPackRegistry<Formation<?,?>> FORMATIONS = RegistryHelper.dataPackRegistry(
            FormationArrays.MOD_ID,
            "formations",
            ()-> FormationType.FORMATIONS
    );



}
