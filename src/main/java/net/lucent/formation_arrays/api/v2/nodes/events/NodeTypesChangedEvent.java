package net.lucent.formation_arrays.api.v2.nodes.events;

import net.lucent.formation_arrays.api.v2.nodes.FormationNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

import java.util.Set;

public class NodeTypesChangedEvent extends Event {
    private final ServerLevel level;
    private final BlockPos pos;
    private final Set<FormationNodeType> added;
    private final Set<FormationNodeType> removed;

    public NodeTypesChangedEvent(ServerLevel level, BlockPos pos, Set<FormationNodeType> added, Set<FormationNodeType> removed) {
        this.level = level;
        this.pos = pos;
        this.added = added;
        this.removed = removed;
    }
}
