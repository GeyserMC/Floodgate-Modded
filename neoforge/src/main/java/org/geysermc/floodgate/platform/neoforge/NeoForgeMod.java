package org.geysermc.floodgate.platform.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.geysermc.floodgate.FloodgateMod;

@Mod("floodgate")
public class NeoForgeMod extends FloodgateMod {

    public NeoForgeMod() {
        this.init(FMLPaths.CONFIGDIR.get().resolve("floodgate"));

        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        if (FMLLoader.getDist().isClient()) {
            NeoForge.EVENT_BUS.addListener(this::onClientStop);
        } else {
            NeoForge.EVENT_BUS.addListener(this::onServerStop);
        }
    }
    private void onServerStarted(ServerStartedEvent event) {
        this.enable(event.getServer());
    }

    private void onClientStop(GameShuttingDownEvent ignored) {
        this.disable();
    }

    private void onServerStop(ServerStoppingEvent ignored) {
        this.disable();
    }
}
