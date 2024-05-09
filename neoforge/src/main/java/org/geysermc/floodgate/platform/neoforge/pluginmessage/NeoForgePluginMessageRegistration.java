package org.geysermc.floodgate.platform.neoforge.pluginmessage;

import lombok.Setter;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageChannel;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageRegistration;
import org.geysermc.floodgate.shared.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.shared.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.shared.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.shared.pluginmessage.payloads.TransferPayload;

public class NeoForgePluginMessageRegistration implements PluginMessageRegistration {

    @Setter
    private static PayloadRegistrar registrar;

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
