package org.geysermc.floodgate.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.geysermc.floodgate.mod.util.ModTemplateReader;
import org.geysermc.floodgate.platform.fabric.module.FabricCommandModule;
import org.geysermc.floodgate.platform.fabric.module.FabricPlatformModule;

import java.nio.file.Path;

public final class FabricFloodgateMod extends FloodgateMod implements ModInitializer {

    private ModContainer container;

    @Override
    public void onInitialize() {
        FabricLoader loader = FabricLoader.getInstance();
        container = loader.getModContainer("floodgate").orElseThrow();

        // Initialize core modules
        init(
            new ServerCommonModule(loader.getConfigDir().resolve("floodgate"), new ModTemplateReader()),
            new FabricPlatformModule(),
            new FabricCommandModule(),
            new PluginMessageModule()
        );

        // Hook into server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(this::enable);

        // Register appropriate shutdown hook depending on environment
        if (loader.getEnvironmentType() == EnvType.CLIENT) {
            ClientLifecycleEvents.CLIENT_STOPPING.register($ -> disable());
        } else {
            ServerLifecycleEvents.SERVER_STOPPING.register($ -> disable());
        }
    }

    @Override
    public @Nullable Path resourcePath(String file) {
        return container.findPath(file).orElse(null);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
