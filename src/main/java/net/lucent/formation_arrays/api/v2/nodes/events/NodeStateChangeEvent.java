package net.lucent.formation_arrays.api.v2.nodes.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

public abstract class NodeStateChangeEvent extends Event {

    private final ServerLevel level;
    private final BlockPos pos;

    public NodeStateChangeEvent(ServerLevel level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public static class Load extends NodeStateChangeEvent{

        public Load(ServerLevel level, BlockPos pos) {
            super(level, pos);
        }
    }
    public static class Unload extends NodeStateChangeEvent{

        public Unload(ServerLevel level, BlockPos pos) {
            super(level, pos);
        }
    }
    public BlockPos getPos() {
        return pos;
    }

    public ServerLevel getLevel() {
        return level;
    }
}
