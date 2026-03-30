package org.geysermc.floodgate.platform.fabric.pluginmessage;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageChannel;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageRegistration;
import org.geysermc.floodgate.mod.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.TransferPayload;

public class FabricPluginMessageRegistration implements PluginMessageRegistration {
    @Override
    public void register(PluginMessageChannel channel) {
        switch (channel.getIdentifier()) {
            case "floodgate:form" -> {
                PayloadTypeRegistry.serverboundPlay().register(FormPayload.TYPE, FormPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(FormPayload.TYPE, FormPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(FormPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().name())));
            }
            case "floodgate:packet" -> {
                PayloadTypeRegistry.serverboundPlay().register(PacketPayload.TYPE, PacketPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(PacketPayload.TYPE, PacketPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(PacketPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().name())));
            }
            case "floodgate:skin" -> {
                PayloadTypeRegistry.serverboundPlay().register(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(SkinPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().name())));
            }
            case "floodgate:transfer" -> {
                PayloadTypeRegistry.serverboundPlay().register(TransferPayload.TYPE, TransferPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(TransferPayload.TYPE, TransferPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(TransferPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().name())));
            }
            default -> throw new IllegalArgumentException("unknown channel: " + channel);
        }
    }
}
