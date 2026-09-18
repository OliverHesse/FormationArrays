package net.lucent.formation_arrays.core.formations;

import io.netty.buffer.ByteBuf;
import net.lucent.formation_arrays.api.CoreRegistries;
import net.lucent.formation_arrays.api.formations.FormationInstance;
import net.lucent.formation_arrays.core.formations.client.ClientDimensionFormationManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DimensionFormationManagerPatch {
    private Set<PlacedFormation> dirty = new HashSet<>();
    private Set<Long> removed = new HashSet<>();

    private ByteBuf cachedBuf;

    public DimensionFormationManagerPatch(Collection<PlacedFormation> dirty, Collection<Long> removed){
        this.dirty = Set.copyOf(dirty);
        this.removed = Set.copyOf(removed);
        cachedBuf = null;
    }
    public DimensionFormationManagerPatch(ByteBuf bytes){
        this.cachedBuf =bytes;
    }
    public Set<PlacedFormation> dirty(){return dirty;}
    public Set<Long> removed(){return removed;}



    public static void encode(RegistryFriendlyByteBuf buf, DimensionFormationManagerPatch patch){
        buf.writeInt(patch.dirty.size());
        for(PlacedFormation formation : patch.dirty()){
            buf.writeLong(formation.id());
            buf.writeIdentifier(formation.instance().getFormationKey(buf.registryAccess()));
            formation.instance().encode(buf,buf.registryAccess());
        }

        buf.writeInt(patch.removed.size());
        for(Long id : patch.removed) buf.writeLong( id);
    }
    public static DimensionFormationManagerPatch decode(RegistryFriendlyByteBuf buf){


        return new DimensionFormationManagerPatch(buf.readBytes(buf.readableBytes()));
    }

    public void applyPatch(ClientDimensionFormationManager manager, RegistryAccess access){
        //if client has formation id tell instance to decode
        //if client does not have id tell formation to decode

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(cachedBuf,access, ConnectionType.OTHER);

        int size = buf.readInt();

        for(int i = 0; i<size; i++){
            long key = buf.readLong();
            Identifier formationId = buf.readIdentifier();

            if(manager.hasFormation(key)) {
                manager.getInstance(key).decode(buf,buf.registryAccess());
                continue;
            }

            FormationInstance<?,?> instance = FormationInstance.initialDecode(buf,access);
            manager.addFormation(new PlacedFormation(key,instance,Set.of()));

        }

        size = buf.readInt();
        for(int i = 0; i<size; i++) manager.removeFormation(buf.readLong());
    }
}
