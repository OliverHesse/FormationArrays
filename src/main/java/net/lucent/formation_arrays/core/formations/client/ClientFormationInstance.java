package net.lucent.formation_arrays.core.formations.client;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Set;

/**
 * An interface that can be used by client exclusive instances to "hide" methods that are never used
 *
 * as well as method wrappers to hide unused parameters
 */
public interface ClientFormationInstance extends FormationInstance {

    @Override
    default boolean tick(Level level, NodeManager manager) {
        tick(level);
        return true;
    }
    @Override
    default void created(Level level,NodeManager manager){created(level);}
    @Override
    default void destroyed(Level level,NodeManager manager){destroyed(level);}

    void tick(Level level);
    void created(Level level);
    void destroyed(Level level);


    @Override
    default void nodeTypesChanged(Level level, NodeManager nodeManager, BlockPos pos){}

    @Override
    default void nodeLoaded(Level level, NodeManager nodeManager,BlockPos pos){}

    @Override
    default void nodeUnloaded(Level level,NodeManager nodeManager,BlockPos pos){}
    @Override
    default boolean isValid(Level level,NodeManager nodeManager){ return false;}
    @Override
    default void write(ValueOutput output, RegistryAccess access) {}
    @Override
    default Set<BlockPos> getListenedNodePositions(){return Set.of();}
    @Override
    default void encode(ByteBuf buf, RegistryAccess access){}
}
