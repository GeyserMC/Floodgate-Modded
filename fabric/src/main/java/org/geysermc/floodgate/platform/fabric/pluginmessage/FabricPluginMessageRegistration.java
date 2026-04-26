package org.geysermc.floodgate.platform.fabric.pluginmessage;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageChannel;
import org.geysermc.floodgate.core.pluginmessage.PluginMessageRegistration;
import org.geysermc.floodgate.mod.pluginmessage.payloads.FormPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.PacketPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.SkinPayload;
import org.geysermc.floodgate.mod.pluginmessage.payloads.TransferPayload;

public class FabricPluginMessageRegistration implements PluginMessageRegistration {

    private final FloodgateApi api;

    public FabricPluginMessageRegistration(FloodgateApi api) {
        this.api = api;
    }

    @Override
    public void register(PluginMessageChannel channel) {
        switch (channel.getIdentifier()) {
            case "floodgate:form" -> {
                PayloadTypeRegistry.serverboundPlay().register(FormPayload.TYPE, FormPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(FormPayload.TYPE, FormPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(FormPayload.TYPE,
                        ((payload, context) -> handleServerCall(channel, payload.data(), context)));
            }
            case "floodgate:packet" -> {
                PayloadTypeRegistry.serverboundPlay().register(PacketPayload.TYPE, PacketPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(PacketPayload.TYPE, PacketPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(PacketPayload.TYPE,
                    ((payload, context) -> handleServerCall(channel, payload.data(), context)));
            }
            case "floodgate:skin" -> {
                PayloadTypeRegistry.serverboundPlay().register(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(SkinPayload.TYPE, SkinPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(SkinPayload.TYPE,
                    ((payload, context) -> handleServerCall(channel, payload.data(), context)));
            }
            case "floodgate:transfer" -> {
                PayloadTypeRegistry.serverboundPlay().register(TransferPayload.TYPE, TransferPayload.STREAM_CODEC);
                PayloadTypeRegistry.clientboundPlay().register(TransferPayload.TYPE, TransferPayload.STREAM_CODEC);
                ServerPlayNetworking.registerGlobalReceiver(TransferPayload.TYPE,
                    ((payload, context) -> handleServerCall(channel, payload.data(), context)));
            }
            default -> throw new IllegalArgumentException("unknown channel: " + channel);
        }
    }

    private void handleServerCall(PluginMessageChannel channel, byte[] data, ServerPlayNetworking.Context context) {
        FloodgatePlayer player = api.getPlayer(context.player().getUUID());
        if (player == null) {
            context.responseSender().disconnect(Component.literal("Only Floodgate players can send Floodgate messages!"));
            return;
        }
        channel.handleServerCall(data, player);
    }
}
