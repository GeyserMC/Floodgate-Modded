package org.geysermc.floodgate.mod.pluginmessage;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.skin.SkinApplier;
import org.geysermc.floodgate.mod.MinecraftServerHolder;
import org.geysermc.floodgate.mod.mixin.ChunkMapMixin;

import java.util.Collections;
import java.util.List;

import static org.geysermc.floodgate.api.event.skin.SkinApplyEvent.SkinData;

public final class ModSkinApplier implements SkinApplier {

    @Override
    public void applySkin(@NonNull FloodgatePlayer floodgatePlayer, @NonNull SkinData skinData) {
        MinecraftServer server = MinecraftServerHolder.get();

        server.execute(() -> {
            ServerPlayer bedrockPlayer = server.getPlayerList().getPlayer(floodgatePlayer.getCorrectUniqueId());
            if (bedrockPlayer == null) {
                // Player has likely disconnected
                return;
            }

            String TEXTURE_KEY = "textures";

            // === Step 1: Update the internal GameProfile with the new skin ===
            PropertyMap properties = bedrockPlayer.getGameProfile().getProperties();
            properties.removeAll(TEXTURE_KEY);
            properties.put(TEXTURE_KEY, new Property(TEXTURE_KEY, skinData.value(), skinData.signature()));

            // === Step 2: Get the entity tracking data ===
            ServerLevel level = (ServerLevel) bedrockPlayer.level;
            ChunkMap chunkMap = level.getChunkSource().chunkMap;
            ChunkMap.TrackedEntity trackedEntity = ((ChunkMapMixin) chunkMap).getEntityMap().get(bedrockPlayer.getId());

            if (trackedEntity == null) {
                // Not currently tracked; nothing to update
                return;
            }

            // === Step 3: Send update packets to all players ===
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            ClientboundPlayerInfoRemovePacket removePacket =
                new ClientboundPlayerInfoRemovePacket(Collections.singletonList(bedrockPlayer.getUUID()));
            ClientboundPlayerInfoUpdatePacket addPacket =
                ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(bedrockPlayer));

            for (ServerPlayer otherPlayer : players) {
                boolean isSelf = otherPlayer == bedrockPlayer;

                if (!isSelf) {
                    // Ensure they no longer track this player before reinitializing
                    trackedEntity.removePlayer(otherPlayer);
                }

                otherPlayer.connection.send(removePacket);
                otherPlayer.connection.send(addPacket);

                // Only update if the players are in the same world
                if (!isSelf && otherPlayer.level == level) {
                    trackedEntity.updatePlayer(otherPlayer);
                }
            }
        });
    }
}
