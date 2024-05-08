package org.geysermc.floodgate.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.geysermc.floodgate.FloodgateMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.geysermc.floodgate.platform.fabric.module.FabricCommandModule;

public abstract class FabricMod extends FloodgateMod implements ModInitializer {

    @Override
    public void onInitialize() {
        this.init(FabricLoader.getInstance().getConfigDir().resolve("floodgate"));
        this.enableCommandModule(new FabricCommandModule());

        ServerLifecycleEvents.SERVER_STARTED.register(this::enable);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientLifecycleEvents.CLIENT_STOPPING.register(($) -> this.disable());
        } else {
            ServerLifecycleEvents.SERVER_STOPPING.register((server) -> this.disable());
        }
    }
}
