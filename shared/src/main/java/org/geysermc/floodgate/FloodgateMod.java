package org.geysermc.floodgate;

import com.google.inject.Module;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import org.geysermc.floodgate.inject.ModInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.fabricmc.loader.api.FabricLoader;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.module.*;
import org.geysermc.floodgate.player.UserAudience;
import org.incendo.cloud.CommandManager;

import java.nio.file.Path;

public abstract class FloodgateMod {

    private boolean started;
    private FloodgatePlatform platform;
    private Injector injector;

    @Getter @Setter
    private static CommandManager<UserAudience> commandManager;

    protected void init(ModPlatformModule module, Path dataDirectory) {
        ModInjector.setInstance(new ModInjector());

        injector = Guice.createInjector(
                new ServerCommonModule(dataDirectory),
                module
        );

        platform = injector.getInstance(FloodgatePlatform.class);
    }

    protected void enableCommandModule(Module postInitializeModules) {
        platform.enable(postInitializeModules);
    }

    protected void enable(MinecraftServer server) {
        long ctm = System.currentTimeMillis();

        // Stupid hack, see the class for more information
        // This can probably be Guice-i-fied but that is beyond me
        MinecraftServerHolder.set(server);

        if (!started) {
            platform.enable(
                    new ModAddonModule(),
                    new ModListenerModule(),
                    new PluginMessageModule()
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
}
