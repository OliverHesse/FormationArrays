package net.lucent.formation_arrays.core.formations.state_handled;

import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.world.level.Level;

import java.util.List;

public interface FormationStateHandler {
    boolean isActive(Level level, NodeManager nodeManager);
    boolean isValid(Level level, NodeManager nodeManager);
}
