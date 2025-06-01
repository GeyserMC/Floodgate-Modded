package org.geysermc.floodgate.mod.data;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import dev.barenton.alias.AliasManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.addon.data.CommonDataHandler;
import org.geysermc.floodgate.core.addon.data.PacketBlocker;
import org.geysermc.floodgate.core.config.FloodgateConfig;
import org.geysermc.floodgate.core.player.FloodgateHandshakeHandler;
import org.geysermc.floodgate.core.player.FloodgateHandshakeHandler.HandshakeResult;
import org.geysermc.floodgate.mod.MinecraftServerHolder;
import org.geysermc.floodgate.mod.mixin.ClientIntentionPacketMixinInterface;
import org.geysermc.floodgate.mod.mixin.ConnectionMixin;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ModDataHandler extends CommonDataHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Pipeline handler names used for inserting/removing handlers
    private static final String SPLITTER_NAME = "splitter";
    private static final String FLOODGATE_BLOCKER_NAME = "floodgate_packet_blocker";
    private static final String PACKET_HANDLER_NAME = "packet_handler";

    // add to your class
    private static final ExecutorService TEXTURE_EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "Bedrock Linked Player Texture Download");
        t.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        return t;
    });

    private final FloodgateLogger logger;
    private Connection networkManager;
    private FloodgatePlayer player;

    public ModDataHandler(
            FloodgateHandshakeHandler handshakeHandler,
            FloodgateConfig config,
            AttributeKey<String> kickMessageAttribute, FloodgateLogger logger) {
        super(handshakeHandler, config, kickMessageAttribute, new PacketBlocker());
        this.logger = logger;
    }

    @Override
    protected void setNewIp(Channel channel, InetSocketAddress newIp) {
        ((ConnectionMixin) this.networkManager).setAddress(newIp);
    }

    @Override
    protected Object setHostname(Object handshakePacket, String hostname) {
        // While it would be ideal to simply create a new handshake packet, the packet constructor
        // does not allow us to set the protocol version
        ((ClientIntentionPacketMixinInterface) handshakePacket).setAddress(hostname);
        return handshakePacket;
    }

    @Override
    protected boolean shouldRemoveHandler(HandshakeResult result) {
        player = result.getFloodgatePlayer();

        if (getKickMessage() != null) {
            // we also have to keep this handler if we want to kick then with a disconnect message
            return false;
        }

        if (player == null) {
            // Player is connecting on Java Edition
            return true;
        }

        if (result.getResultType() == FloodgateHandshakeHandler.ResultType.SUCCESS) {
            logger.info("[Alias-Floodgate] {} is connecting...",
                    player.getCorrectUsername());
        }

        // Handler will be removed after the login hello packet is handled
        return false;
    }

    @Override
    protected boolean channelRead(Object packet) {
        if (packet instanceof ClientIntentionPacket intentionPacket) {
            ctx.pipeline().addAfter(SPLITTER_NAME, FLOODGATE_BLOCKER_NAME, blocker);
            networkManager = (Connection) ctx.channel().pipeline().get(PACKET_HANDLER_NAME);
            handle(packet, intentionPacket.hostName());
            return false;
        }
        return !checkAndHandleLogin(packet);
    }

    private boolean checkAndHandleLogin(Object packet) {
        if (!(packet instanceof ServerboundHelloPacket)) {
            return false;
        }

        String kickMessage = getKickMessage();
        if (kickMessage != null) {
            Component message = Component.nullToEmpty(kickMessage);
            // If possible, disconnect using the "proper" packet listener; otherwise there's no proper disconnect message
            if (networkManager.getPacketListener() instanceof ServerLoginPacketListenerImpl loginPacketListener) {
                loginPacketListener.disconnect(message);
            } else {
                networkManager.disconnect(message);
            }
            return true;
        }

        // we have to fake the offline player (login) cycle
        if (!(networkManager.getPacketListener() instanceof ServerLoginPacketListenerImpl packetListener)) {
            // player is not in the login state, abort
            ctx.pipeline().remove(this);
            return true;
        }

        GameProfile gameProfile = getProfile(player, logger);

        if (player.isLinked() && player.getCorrectUniqueId().version() == 4) {
            verifyLinkedPlayerAsync(packetListener, gameProfile);
        } else {
            packetListener.startClientVerification(gameProfile);
        }

        ctx.pipeline().remove(this);
        return true;
    }

    private GameProfile getProfile(FloodgatePlayer player, FloodgateLogger logger) {
        String name = player.getCorrectUsername();
        UUID uuid = player.getCorrectUniqueId();

        if (AliasManager.hasRedirect(name)) {
            GameProfile redirected = AliasManager.resolve(name);
            logger.info("[Alias] Remapping Bedrock profile: {} → {}", name, redirected.getName());
            return redirected;
        }
        return new GameProfile(uuid, name);
    }

    private void verifyLinkedPlayerAsync(ServerLoginPacketListenerImpl listener, GameProfile initial) {
        TEXTURE_EXECUTOR.submit(() -> {
            GameProfile effective = initial;
            try {
                effective = MinecraftServerHolder.get()
                        .getSessionService()
                        .fetchProfile(initial.getId(), true)
                        .profile();
            } catch (Exception e) {
                LOGGER.error("Unable to get Bedrock linked player textures for {}", initial.getName(), e);
            }
            listener.startClientVerification(effective);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (config.isDebug()) {
            LOGGER.error("Exception caught in FabricDataHandler", cause);
        }
    }
}
