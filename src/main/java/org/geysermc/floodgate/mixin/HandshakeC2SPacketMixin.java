package org.geysermc.floodgate.mixin;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(HandshakeC2SPacket.class)
public interface HandshakeC2SPacketMixin {

    @Accessor("address")
    void setAddress(String address);

}
