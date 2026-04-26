package org.geysermc.floodgate.mod.pluginmessage;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.inject.Singleton;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.floodgate.api.event.skin.SkinApplyEvent;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.event.EventBus;
import org.geysermc.floodgate.core.event.skin.SkinApplyEventImpl;
import org.geysermc.floodgate.core.skin.SkinApplier;
import org.geysermc.floodgate.core.skin.SkinDataImpl;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.geysermc.floodgate.mod.MinecraftServerHolder;
import org.geysermc.floodgate.mod.mixin.ChunkMapMixin;
import org.geysermc.floodgate.mod.mixin.PlayerAccessor;

import java.util.Collections;

import static org.geysermc.floodgate.api.event.skin.SkinApplyEvent.SkinData;

@Singleton
public final class ModSkinApplier implements SkinApplier {

    private final EventBus eventBus;

    public ModSkinApplier(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void applySkin(@NonNull FloodgatePlayer floodgatePlayer, @NonNull SkinData skinData, boolean internal) {
        applySkin0(floodgatePlayer, skinData, true, internal);
    }

    public void applySkin0(@NonNull FloodgatePlayer floodgatePlayer, @NonNull SkinData skinData, boolean firstTry, boolean internal) {
        MinecraftServerHolder.get().execute(() -> {
            ServerPlayer bedrockPlayer = MinecraftServerHolder.get().getPlayerList()
                .getPlayer(floodgatePlayer.getCorrectUniqueId());
            if (bedrockPlayer == null) {
                if (firstTry) {
                    // try again later!
                    FloodgateMod.INSTANCE.schedule(() -> applySkin0(floodgatePlayer, skinData, false, internal), 20 * 10);
                }
                return;
            }

            SkinData currentSkin = currentSkin(bedrockPlayer.getGameProfile());

            SkinApplyEvent event = new SkinApplyEventImpl(floodgatePlayer, currentSkin, skinData);
            event.setCancelled(!internal && floodgatePlayer.isLinked());

            eventBus.fire(event);

            if (event.isCancelled()) {
                return;
            }

            // Apply the new skin internally
            Multimap<String, Property> properties = MultimapBuilder.hashKeys().arrayListValues().build();
            properties.put("textures", new Property("textures", skinData.value(), skinData.signature()));
            ((PlayerAccessor) bedrockPlayer).setGameProfile(new GameProfile(bedrockPlayer.getGameProfile().id(),
                bedrockPlayer.getGameProfile().name(), new PropertyMap(properties)));

            ChunkMap tracker = ((ServerLevel) bedrockPlayer.level).getChunkSource().chunkMap;
            ChunkMap.TrackedEntity entry = ((ChunkMapMixin) tracker).getEntityMap().get(bedrockPlayer.getId());
            // Skin is applied - now it's time to refresh the player for everyone.
            for (ServerPlayer otherPlayer : MinecraftServerHolder.get().getPlayerList().getPlayers()) {
                boolean samePlayer = otherPlayer == bedrockPlayer;
                if (!samePlayer) {
                    // TrackedEntity#broadcastRemoved doesn't actually remove them from seenBy
                    entry.removePlayer(otherPlayer);
                }

                otherPlayer.connection.send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(bedrockPlayer.getUUID())));
                otherPlayer.connection.send(ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(bedrockPlayer)));
                if (samePlayer) {
                    continue;
                }

                if (bedrockPlayer.level == otherPlayer.level) {
                    entry.updatePlayer(otherPlayer);
                }
            }
        });
    }

    public SkinApplyEvent.SkinData currentSkin(GameProfile profile) {
        PropertyMap properties = profile.properties();

        for (Property property : properties.get("textures")) {
            String value = property.value();
            String signature = property.signature();

            if (!value.isEmpty()) {
                //noinspection DataFlowIssue
                return new SkinDataImpl(value, signature);
            }
        }
        return null;
    }
}
