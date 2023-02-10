package org.geysermc.floodgate.mixin;

import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.geysermc.floodgate.util.ProxyUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientIntentionPacket.class)
public class ClientIntentionPacketMixin {
    @ModifyConstant(method = "<init>*", constant = @Constant(intValue = 255))
    private int floodgate$modifyMaxCapacity(int defaultValue) {
        if (ProxyUtils.isProxyData()) {
            //let's not modify if unnecessary
            return Short.MAX_VALUE;
        }
        return defaultValue;
    }
}
