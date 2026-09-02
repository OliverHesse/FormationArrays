package net.lucent.formation_arrays.core.formations.client;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.Formation;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.lucent.formation_arrays.api.nodes.NodeManager;
import net.lucent.formation_arrays.core.formations.TickableFormation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

public class TickableFormationClientInstance<T extends FormationRuntimeData> implements ClientFormationInstance {
    private T runtimeData;
    private final TickableFormation<T,?> tickableFormation;

    private boolean wasActive = false;
    private boolean active = false;
    public TickableFormationClientInstance(TickableFormation<T, ?> tickableFormation, T runtimeData) {
        this.tickableFormation = tickableFormation;
        this.runtimeData = runtimeData;

    }
    @Override
    public void tick(Level level) {
        if(wasActive && !active){
            tickableFormation.deactivate(runtimeData,level);
            return;
        }else if(!wasActive && active){
            tickableFormation.activate(runtimeData,level);
        }
        wasActive = active;
        if(active) tickableFormation.tick(runtimeData,level);
    }

    @Override
    public void created(Level level) {

    }

    @Override
    public void destroyed(Level level) {
        tickableFormation.destroy(runtimeData,level);
    }

    @Override
    public Formation<?, ?> getFormation() {
        return tickableFormation;
    }

    public void setActive(boolean state){active = state;}

    @Override
    public void decode(ByteBuf buf, RegistryAccess access) {
        active = buf.readBoolean();
        runtimeData = tickableFormation.loadRuntimeData(buf,access);
    }
}
