package net.lucent.formation_arrays.core.formations;

import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.minecraft.world.level.Level;

public interface TickableFormation<T extends FormationRuntimeData,S extends FormationInstance> extends Formation<T,S> {
    void activate(T runtimeData, Level level);
    void deactivate(T runtimeData,Level level);
    void destroy(T runtimeData,Level level);

    /**
     * @param runtimeData the current runtime data for this formation instance
     * @param level the level it was ticked in
     * @return true -> dirty so trigger sync, false -> not dirty no sync
     */
    boolean tick(T runtimeData, Level level);
}
