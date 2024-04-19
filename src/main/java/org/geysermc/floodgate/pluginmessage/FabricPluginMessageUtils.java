package org.geysermc.floodgate.pluginmessage;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.geysermc.floodgate.MinecraftServerHolder;
import org.geysermc.floodgate.platform.pluginmessage.PluginMessageUtils;

import java.util.Objects;
import java.util.UUID;

public class FabricPluginMessageUtils extends PluginMessageUtils {

    @Override
    public boolean sendMessage(UUID uuid, String channel, byte[] data) {
        try {
            ServerPlayer player = MinecraftServerHolder.get().getPlayerList().getPlayer(uuid);
            ResourceLocation resource = new ResourceLocation(channel); // automatically splits over the :
            FriendlyByteBuf dataBuffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
            Objects.requireNonNull(player);
            ServerPlayNetworking.send(player, resource, dataBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
