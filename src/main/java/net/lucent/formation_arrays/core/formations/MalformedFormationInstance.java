package net.lucent.formation_arrays.core.formations;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Set;

public class MalformedFormationInstance implements FormationInstance {
    @Override
    public boolean tick(Level level, NodeManager manager) {
        return false;
    }

    @Override
    public void created(Level level, NodeManager manager) {

    }

    @Override
    public void destroyed(Level level, NodeManager manager) {

    }

    @Override
    public Formation<?, ?> getFormation() {
        return null;
    }

    @Override
    public Set<BlockPos> getListenedNodePositions() {
        return Set.of();
    }

    @Override
    public void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public void nodeLoaded(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public void nodeUnloaded(Level level, NodeManager nodeManager, BlockPos pos) {

    }

    @Override
    public boolean isValid(Level level, NodeManager nodeManager) {
        return false;
    }

    @Override
    public void write(ValueOutput output, RegistryAccess access) {

    }

    @Override
    public void encode(ByteBuf buf, RegistryAccess access) {

    }

    @Override
    public void decode(ByteBuf buf, RegistryAccess access) {

    }
}
