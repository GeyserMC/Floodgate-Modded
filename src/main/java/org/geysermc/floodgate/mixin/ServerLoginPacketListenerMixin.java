package org.geysermc.floodgate.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerLoginPacketListenerImpl.class)
public interface ServerLoginPacketListenerMixin {

    @Invoker("startClientVerification")
    void startClientVerification(GameProfile profile);
}
