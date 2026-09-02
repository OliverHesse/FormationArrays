package net.lucent.formation_arrays.test;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.formations.FormationRuntimeData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TickCounterRuntimeData implements FormationRuntimeData {
    int ticks = 0;

    public void read(ValueInput input){
        ticks = input.getIntOr("ticks",0);
    }
    public void write(ValueOutput output){
        output.putInt("ticks",ticks);
    }
    public void decode(ByteBuf buf){this.ticks = buf.readInt();}
    public void encode(ByteBuf buf){buf.writeInt(ticks);}
}
