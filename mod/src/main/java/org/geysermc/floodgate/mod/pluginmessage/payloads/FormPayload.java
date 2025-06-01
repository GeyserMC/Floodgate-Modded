package org.geysermc.floodgate.mod.pluginmessage.payloads;

import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public record FormPayload(byte[] data) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, FormPayload> STREAM_CODEC =
        CustomPacketPayload.codec(FormPayload::write, FormPayload::read);

    public static final Type<FormPayload> TYPE = new Type<>(ResourceLocation.parse("floodgate:form"));

    private static FormPayload read(FriendlyByteBuf buf) {
        int readable = buf.readableBytes();
        byte[] bytes = ByteBufUtil.getBytes(buf, buf.readerIndex(), readable, false);
        buf.skipBytes(readable); // Advance manually instead of relying on ByteBufUtil
        return new FormPayload(bytes);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBytes(this.data);
    }

    @Override
    public @NonNull Type<FormPayload> type() {
        return TYPE;
    }
}
