package net.lucent.formation_arrays.api.v2;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.lucent.formation_arrays.api.v2.formations.FormationDefinition;
import net.minecraft.core.Registry;
import net.zic.zenithlib.registry.RegistryHelper;

public class CoreRegistries {

    public static final Registry<Formation> FORMATIONS = RegistryHelper.registry(FormationArrays.MOD_ID,"formations");

    public static final RegistryHelper.DataPackRegistry<FormationDefinition> FORMATION_DEFINITIONS = RegistryHelper.dataPackRegistry(
            FormationArrays.MOD_ID,
            "formations",
            ()-> Formation.FORMATION_DEFINITIONS
    );



}
