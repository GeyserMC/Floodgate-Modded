package org.geysermc.floodgate.platform.fabric.listener;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.geysermc.floodgate.shared.listener.ModEventListener;
import org.geysermc.floodgate.core.platform.listener.ListenerRegistration;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class FabricEventRegistration implements ListenerRegistration<ModEventListener> {
    @Override
    public void register(ModEventListener listener) {
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> listener.onPlayerJoin(handler.getPlayer().getUUID())
        );
    }
}
