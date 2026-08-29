package net.lucent.formation_arrays.api.v2.formations;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

public interface Formation<T extends FormationRuntimeData,S extends FormationStateHandler>{

    FormationType getType();

    void tick(T runtimeData, S state, Level level);
    FormationInstance createFreshFormation();
    FormationInstance loadFormation(ValueInput input);
}
