package org.geysermc.floodgate.pluginmessage;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.geysermc.floodgate.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.pluginmessage.payloads.TransferPayload;

public class FabricPluginMessageRegistration implements PluginMessageRegistration {
    @Override
    public void register(PluginMessageChannel channel) {
        System.out.println("registering channel: " + channel.getIdentifier());

        switch (channel.getIdentifier()) {
            case "floodgate:form" -> {
                PayloadTypeRegistry.playS2C().register(FormPayload.TYPE, FormPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(FormPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().getName())));
            }
            case "floodgate:packet" -> {
                PayloadTypeRegistry.playS2C().register(PacketPayload.TYPE, PacketPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(PacketPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().getName())));
            }
            case "floodgate:skin" -> {
                PayloadTypeRegistry.playS2C().register(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(SkinPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().getName())));
            }
            case "floodgate:transfer" -> {
                PayloadTypeRegistry.playS2C().register(TransferPayload.TYPE, TransferPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(TransferPayload.TYPE,
                        ((payload, context) -> channel.handleServerCall(
                                payload.data(),
                                context.player().getUUID(),
                                context.player().getGameProfile().getName())));
            }
            default -> throw new IllegalArgumentException("unknown channel: " + channel);
        }
    }
}
