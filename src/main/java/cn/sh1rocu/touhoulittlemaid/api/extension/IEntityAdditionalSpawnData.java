package cn.sh1rocu.touhoulittlemaid.api.extension;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

// Porting_Lib
public interface IEntityAdditionalSpawnData {
    ResourceLocation EXTRA_DATA_PACKET = new ResourceLocation(TouhouLittleMaid.MOD_ID, "extra_entity_spawn_data");

    void readSpawnData(FriendlyByteBuf buf);

    void writeSpawnData(FriendlyByteBuf buf);

    static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return new ClientboundAddEntityPacket(entity);
    }

    static Packet<?> additionalData(Entity entity, IEntityAdditionalSpawnData extra) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(entity.getId());
        extra.writeSpawnData(buf);
        return ServerPlayNetworking.createS2CPacket(EXTRA_DATA_PACKET, buf);
    }
}
