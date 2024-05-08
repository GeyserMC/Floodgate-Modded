package org.geysermc.floodgate.platform.neoforge.pluginmessage;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.geysermc.floodgate.pluginmessage.PluginMessageChannel;
import org.geysermc.floodgate.pluginmessage.PluginMessageRegistration;
import org.geysermc.floodgate.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.pluginmessage.payloads.TransferPayload;

public class NeoForgePluginMessageRegistration implements PluginMessageRegistration {

    private PayloadRegistrar registrar;

    @SubscribeEvent
    private void onRegisterPackets(final RegisterPayloadHandlersEvent event) {
        this.registrar = event.registrar("floodgate").optional();
    }

    @Override
    public void register(PluginMessageChannel channel) {
        switch (channel.getIdentifier()) {
            case "floodgate:form" ->
                    registrar.playBidirectional(FormPayload.TYPE, FormPayload.STREAM_CODEC, (arg, iPayloadContext) -> {
                        channel.handleServerCall(arg.data(), iPayloadContext.player().getUUID(),
                                iPayloadContext.player().getGameProfile().getName());
                    });
            case "floodgate:packet" ->
                    registrar.playBidirectional(PacketPayload.TYPE, PacketPayload.STREAM_CODEC, (arg, iPayloadContext) -> {
                        channel.handleServerCall(arg.data(), iPayloadContext.player().getUUID(),
                                iPayloadContext.player().getGameProfile().getName());
                    });
            case "floodgate:skin" ->
                    registrar.playBidirectional(SkinPayload.TYPE, SkinPayload.STREAM_CODEC, (arg, iPayloadContext) -> {
                        channel.handleServerCall(arg.data(), iPayloadContext.player().getUUID(),
                                iPayloadContext.player().getGameProfile().getName());
                    });
            case "floodgate:transfer" ->
                    registrar.playBidirectional(TransferPayload.TYPE, TransferPayload.STREAM_CODEC, (arg, iPayloadContext) -> {
                        channel.handleServerCall(arg.data(), iPayloadContext.player().getUUID(),
                                iPayloadContext.player().getGameProfile().getName());
                    });
            default -> throw new IllegalArgumentException("unknown channel: " + channel);
        }
    }
}
