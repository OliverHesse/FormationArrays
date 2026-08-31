package net.lucent.formation_arrays.api.formations;

import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * Interfaces for the formation manager to "handle" the formation, should hold
 * the runtime data and state handler, but because the manager never directly interfaces with those
 * we do not need them as generics, since
 */
public interface FormationInstance{
    void tick(Level level,NodeManager manager);

    //tells the instance it has officially been created
    void created(Level level,NodeManager manager);
    //tells the instance it has officially been destroyed
    void destroyed(Level level,NodeManager manager);

    Formation<?,?> getFormation();

    //all the positions the formations wants updates for
    Set<BlockPos> getListenedNodePositions();


    //tells the formation to update its state for this pos
    void nodeTypesChanged(Level level, NodeManager nodeManager,BlockPos pos);

    //tells the formations that a listened node was loaded
    void nodeLoaded(Level level, NodeManager nodeManager,BlockPos pos);

    //tells the formation that a listened node was unloaded
    void nodeUnloaded(Level level,NodeManager nodeManager,BlockPos pos);

    /**
     * called after nodeTypesChanged, used to determine if the formation should be destroyed or not
     * (while nodeManager is provided it is recommended to rely on an internal state
     * @param level the level this happens in
     * @return true -> do nothing, false -> destroy
     */
    boolean isValid(Level level,NodeManager nodeManager);


}
