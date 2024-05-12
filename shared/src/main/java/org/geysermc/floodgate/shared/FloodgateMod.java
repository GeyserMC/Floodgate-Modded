package org.geysermc.floodgate.shared;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.core.FloodgatePlatform;
import org.geysermc.floodgate.core.player.UserAudience;
import org.geysermc.floodgate.shared.inject.ModInjector;
import org.geysermc.floodgate.shared.module.ModAddonModule;
import org.geysermc.floodgate.shared.module.ModListenerModule;
import org.incendo.cloud.CommandManager;

import java.nio.file.Path;

public abstract class FloodgateMod {
    public static FloodgateMod INSTANCE;

    private boolean started;
    private FloodgatePlatform platform;
    private Injector injector;

    @Getter @Setter
    private static CommandManager<UserAudience> commandManager;

    protected void init(Module... modules) {
        INSTANCE = this;
        ModInjector.setInstance(new ModInjector());
        injector = Guice.createInjector(modules);
        platform = injector.getInstance(FloodgatePlatform.class);
    }

    protected void enable(MinecraftServer server) {
        long ctm = System.currentTimeMillis();

        // Stupid hack, see the class for more information
        // This can probably be Guice-i-fied but that is beyond me
        MinecraftServerHolder.set(server);

        if (!started) {
            platform.enable(
                    new ModAddonModule(),
                    new ModListenerModule()
            );
            started = true;
        }

        long endCtm = System.currentTimeMillis();
        injector.getInstance(FloodgateLogger.class)
                .translatedInfo("floodgate.core.finish", endCtm - ctm);
    }

    protected void disable() {
        platform.disable();
    }

    protected void enable(Module... module) {
        platform.enable(module);
    }

    public @Nullable abstract Path getResourcePath(String file);
}
