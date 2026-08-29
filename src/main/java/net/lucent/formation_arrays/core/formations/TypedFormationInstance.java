package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.api.v2.CoreRegistries;
import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.lucent.formation_arrays.api.v2.formations.FormationInstance;
import net.lucent.formation_arrays.api.v2.formations.FormationRuntimeData;
import net.lucent.formation_arrays.api.v2.formations.FormationStateHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public record TypedFormationInstance <T extends FormationRuntimeData,S extends FormationStateHandler>(
        Formation<T,S> formation,
        T runtimeData,
        S handler
) implements FormationInstance {

    @Override
    public void tick(Level level){
        formation.tick(runtimeData,handler,level);
    }

    //important!! since this is saved so we can retrieve the proper instance
    public Identifier getFormationId(RegistryAccess access){
        return CoreRegistries.FORMATIONS.get(access).getKey(formation);
    }
}
