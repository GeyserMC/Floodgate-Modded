package org.geysermc.floodgate.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.geysermc.floodgate.mod.util.ModTemplateReader;
import org.geysermc.floodgate.platform.fabric.module.FabricCommandModule;
import org.geysermc.floodgate.platform.fabric.module.FabricPlatformModule;
import org.geysermc.floodgate.platform.fabric.util.TaskTimer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FabricFloodgateMod extends FloodgateMod implements ModInitializer {

    private ModContainer container;

    @Override
    public void onInitialize() {
        container = FabricLoader.getInstance().getModContainer("floodgate").orElseThrow();
        init(
            new ServerCommonModule(
                    FabricLoader.getInstance().getConfigDir().resolve("floodgate"),
                    new ModTemplateReader()
            ),
            new FabricPlatformModule(),
            new FabricCommandModule(),
            new PluginMessageModule()
        );

        ServerLifecycleEvents.SERVER_STARTED.register(this::enable);

        if (isClient()) {
            ClientLifecycleEvents.CLIENT_STOPPING.register($ -> this.disable());
        } else {
            ServerLifecycleEvents.SERVER_STOPPING.register($ -> this.disable());
        }
        TaskTimer.register();
    }

    @Override
    public @NonNull InputStream resourceStream(String file) throws IOException {
        Path path = container.findPath(file).orElseThrow();
        return Files.newInputStream(path);
    }

    @Override
    public void schedule(Runnable runnable, int ticks) {
        TaskTimer.INSTANCE.runLater(runnable, ticks);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
