package org.geysermc.floodgate.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.platform.fabric.module.FabricCommandModule;
import org.geysermc.floodgate.platform.fabric.module.FabricPlatformModule;
import org.geysermc.floodgate.platform.fabric.util.FabricTemplateReader;
import org.geysermc.floodgate.shared.FloodgateMod;

public final class FabricMod extends FloodgateMod implements ModInitializer {

    @Override
    public void onInitialize() {
        this.init(
                new ServerCommonModule(
                        FabricLoader.getInstance().getConfigDir().resolve("floodgate"),
                        new FabricTemplateReader()
                ),
                new FabricPlatformModule());
        this.enableCommandModule(new FabricCommandModule());

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
                enable(server);
                enable(new PluginMessageModule());
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientLifecycleEvents.CLIENT_STOPPING.register(($) -> this.disable());
        } else {
            ServerLifecycleEvents.SERVER_STOPPING.register((server) -> this.disable());
        }
    }
}
