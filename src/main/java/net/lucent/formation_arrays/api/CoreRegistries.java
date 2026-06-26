package net.lucent.formation_arrays.api;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.minecraft.core.Registry;
import net.zic.zenithlib.registry.RegistryHelper;

public class CoreRegistries {
    public static final Registry<FormationType> FORMATION_TYPES = RegistryHelper.registry(FormationArrays.MOD_ID,"formation_types");

    public static final RegistryHelper.DataPackRegistry<Formation> FORMATIONS = RegistryHelper.dataPackRegistry(
            FormationArrays.MOD_ID,
            "physiques",
            ()-> FormationType.PHYSIQUE_CODEC
    );
}
