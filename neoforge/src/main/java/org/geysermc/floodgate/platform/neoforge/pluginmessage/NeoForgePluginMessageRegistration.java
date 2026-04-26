package org.geysermc.floodgate.platform.neoforge.pluginmessage;

import com.google.inject.Inject;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageChannel;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageRegistration;
import org.geysermc.floodgate.mod.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.TransferPayload;

public class NeoForgePluginMessageRegistration implements PluginMessageRegistration {

    @Setter
    private PayloadRegistrar registrar;

    private final FloodgateApi api;
    
    @Inject
    public NeoForgePluginMessageRegistration(FloodgateApi api) {
        this.api = api;
    }

    @Override
    public void register(PluginMessageChannel channel) {
        switch (channel.getIdentifier()) {
            case "floodgate:form" ->
                    registrar.playBidirectional(FormPayload.TYPE, FormPayload.STREAM_CODEC, (payload, context) ->
                            handleServerCall(channel, payload.data(), context));
            case "floodgate:packet" ->
                    registrar.playBidirectional(PacketPayload.TYPE, PacketPayload.STREAM_CODEC, (payload, context) ->
                            handleServerCall(channel, payload.data(), context));
            case "floodgate:skin" ->
                    registrar.playBidirectional(SkinPayload.TYPE, SkinPayload.STREAM_CODEC, (payload, context) ->
                            handleServerCall(channel, payload.data(), context));
            case "floodgate:transfer" ->
                    registrar.playBidirectional(TransferPayload.TYPE, TransferPayload.STREAM_CODEC, (payload, context) ->
                            handleServerCall(channel, payload.data(), context));
            default -> throw new IllegalArgumentException("unknown channel: " + channel);
        }
    }

    private void handleServerCall(PluginMessageChannel channel, byte[] data, IPayloadContext context) {
        FloodgatePlayer player = api.getPlayer(context.player().getUUID());
        if (player == null) {
            context.disconnect(Component.literal("Only Floodgate players can send floodgate messages!"));
            return;
        }
        channel.handleServerCall(data, player);
    }
}
