package org.geysermc.floodgate.listener;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.geysermc.floodgate.platform.listener.ListenerRegistration;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class FabricEventRegistration extends ListenerRegistration<ModEventListener> {
    @Override
    public void register(ModEventListener listener) {
        ServerPlayConnectionEvents.JOIN.register(listener::onPlayerJoin);
    }
}
