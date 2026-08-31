package net.lucent.formation_arrays.api;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.lucent.formation_arrays.api.formations.Formation;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.zic.zenithlib.registry.RegistryHelper;

public class CoreRegistries {

    public static final Registry<FormationType> FORMATION_TYPES = RegistryHelper.registry(FormationArrays.MOD_ID,"formations");

    public static final RegistryHelper.DataPackRegistry<Formation<?,?>> FORMATIONS = RegistryHelper.dataPackRegistry(
            FormationArrays.MOD_ID,
            "formations",
            ()-> FormationType.FORMATIONS
    );

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event){
        event.register(FORMATION_TYPES);
    }
    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        register(event,FORMATIONS);
    }

    private static <T> void register(DataPackRegistryEvent.NewRegistry event, RegistryHelper.DataPackRegistry<T> registry) {
        event.dataPackRegistry(registry.key(), registry.codec().get(), registry.codec().get());
    }
}
