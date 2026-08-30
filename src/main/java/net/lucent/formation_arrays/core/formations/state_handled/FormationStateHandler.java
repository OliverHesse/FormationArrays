package net.lucent.formation_arrays.core.formations.state_handled;

import net.minecraft.world.level.Level;

public abstract class FormationStateHandler {

    public abstract boolean isActive(Level level);
    public abstract boolean isValid(Level level);
}
