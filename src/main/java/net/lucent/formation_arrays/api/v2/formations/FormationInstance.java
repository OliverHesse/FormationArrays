package net.lucent.formation_arrays.api.v2.formations;

import net.minecraft.world.level.Level;

/**
 * Interfaces for the formation manager to "handle" the formation, should hold
 * the runtime data and state handler, but because the manager never directly interfaces with those
 * we do not need them as generics, since
 */
public interface FormationInstance{
    void tick(Level level);
}
