package org.geysermc.floodgate.addon.data;

import static org.geysermc.floodgate.util.ReflectionUtils.getField;
import static org.geysermc.floodgate.util.ReflectionUtils.setValue;


import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.geysermc.floodgate.mixin_interface.ServerLoginNetworkHandlerSetter;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.config.FloodgateConfig;
import org.geysermc.floodgate.player.FloodgateHandshakeHandler;
import org.geysermc.floodgate.player.FloodgateHandshakeHandler.HandshakeResult;

import java.net.InetSocketAddress;

public final class FabricDataHandler extends CommonDataHandler{

    private ClientConnection networkManager;
    private FloodgatePlayer player;

    public FabricDataHandler(
            FloodgateHandshakeHandler handshakeHandler,
            FloodgateConfig config,
            AttributeKey<String> kickMessageAttribute, PacketBlocker blocker){
            super(handshakeHandler, config, kickMessageAttribute, new PacketBlocker());
    }

    @Override
    protected void setNewIp(Channel channel, InetSocketAddress newIp) {
        setValue(networkManager, getField(ClientConnection.class, "address"), newIp);
    }

    @Override
    protected Object setHostname(Object handshakePacket, String hostname) {
        setValue(handshakePacket, getField(HandshakeC2SPacket.class, "address"), hostname);
        return handshakePacket;
    }

    @Override
    protected boolean shouldRemoveHandler(HandshakeResult result) {
        //TODO
        return true;
    }

    @Override
    protected boolean channelRead(Object packet){
        //TODO
        if (packet instanceof HandshakeC2SPacket handshakePacket) {
            
            return false;
        }
        return true;
    }

    private boolean checkAndHandleLogin(Object packet) {
        if (packet instanceof LoginHelloC2SPacket) {
            // we have to fake the offline player (login) cycle
            if (!(networkManager.getPacketListener() instanceof ServerLoginNetworkHandler)) {
                // player is not in the login state, abort
                ctx.pipeline().remove(this);
                return true;
            }

            GameProfile gameProfile = new GameProfile(player.getCorrectUniqueId(), player.getCorrectUsername());

            ((ServerLoginNetworkHandlerSetter) networkManager.getPacketListener()).setGameProfile(gameProfile);
            ((ServerLoginNetworkHandlerSetter) networkManager.getPacketListener()).setLoginState();

            ctx.pipeline().remove(this);
            return true;
        }
        return false;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        if (config.isDebug()) {
            cause.printStackTrace();
        }
    }
}
