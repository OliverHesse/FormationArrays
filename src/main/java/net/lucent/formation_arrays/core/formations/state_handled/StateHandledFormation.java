package net.lucent.formation_arrays.core.formations.state_handled;

import net.lucent.formation_arrays.api.v2.formations.Formation;
import net.lucent.formation_arrays.api.v2.formations.FormationInstance;
import net.lucent.formation_arrays.api.v2.formations.FormationRuntimeData;
import net.minecraft.world.level.Level;

public interface StateHandledFormation<T extends FormationRuntimeData,S extends FormationStateHandler> extends Formation<T,StateHandledFormationInstance<T,S>> {
    void activate(T runtimeData, S state, Level level);
    void deactivate(T runtimeData,S state,Level level);
    void destroy(T runtimeData,S state,Level level);
    void tick(T runtimeData, S state, Level level);
}
