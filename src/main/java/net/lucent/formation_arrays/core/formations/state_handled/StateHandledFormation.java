package net.lucent.formation_arrays.core.formations.state_handled;

import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.lucent.formation_arrays.core.formations.TickableFormation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

public interface StateHandledFormation<T extends FormationRuntimeData,S extends FormationStateHandler> extends TickableFormation<T,StateHandledFormationInstance<T,S>> {


}
