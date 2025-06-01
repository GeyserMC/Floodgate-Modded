package org.geysermc.floodgate.mod.pluginmessage.payloads;

import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public record TransferPayload(byte[] data) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, TransferPayload> STREAM_CODEC =
        CustomPacketPayload.codec(TransferPayload::write, TransferPayload::read);

    public static final Type<TransferPayload> TYPE = new Type<>(ResourceLocation.parse("floodgate:transfer"));

    private static TransferPayload read(FriendlyByteBuf buf) {
        int readable = buf.readableBytes();
        byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), readable, false);
        buf.skipBytes(readable); // Advance manually instead of relying on ByteBufUtil
        return new TransferPayload(bytes);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBytes(this.data);
    }

    @Override
    public @NonNull Type<TransferPayload> type() {
        return TYPE;
    }
}
