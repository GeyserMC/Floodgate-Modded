package org.geysermc.floodgate.platform.neoforge.listener;


import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.geysermc.floodgate.shared.listener.ModEventListener;
import org.geysermc.floodgate.core.platform.listener.ListenerRegistration;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public final class NeoForgeEventRegistration implements ListenerRegistration<ModEventListener> {
    private ModEventListener listener;
    @Override
    public void register(ModEventListener listener) {
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        this.listener = listener;
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        listener.onPlayerJoin(event.getEntity().getUUID());
    }
}
