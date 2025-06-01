package org.geysermc.floodgate.mod.pluginmessage.payloads;

import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public record PacketPayload(byte[] data) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, PacketPayload> STREAM_CODEC =
        CustomPacketPayload.codec(PacketPayload::write, PacketPayload::read);

    public static final Type<PacketPayload> TYPE = new Type<>(ResourceLocation.parse("floodgate:packet"));

    private static PacketPayload read(FriendlyByteBuf buf) {
        int readable = buf.readableBytes();
        byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), readable, false);
        buf.skipBytes(readable); // Advance manually instead of relying on ByteBufUtil
        return new PacketPayload(bytes);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBytes(this.data);
    }

    @Override
    public @NonNull Type<PacketPayload> type() {
        return TYPE;
    }
}
