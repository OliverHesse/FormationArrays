package net.lucent.formation_arrays.test;

import net.lucent.formation_arrays.FormationArrays;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.formations.FormationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestRegistries {
    public static final DeferredRegister<FormationType> FORMATION_TYPES =
            DeferredRegister.create(CoreRegistries.FORMATION_TYPES, FormationArrays.MOD_ID);

    public static final DeferredHolder<FormationType,FormationType> DEBUG_FORMATION_TYPE = FORMATION_TYPES.register(
            "debug",
            DebugTestFormationType::new
    );

    public static void register(IEventBus eventBus){

        FORMATION_TYPES.register(eventBus);
    }
}
