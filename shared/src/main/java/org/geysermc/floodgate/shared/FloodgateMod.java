package org.geysermc.floodgate.shared;

import com.google.inject.Module;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import org.geysermc.floodgate.core.FloodgatePlatform;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.shared.inject.ModInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.core.player.UserAudience;
import org.geysermc.floodgate.shared.module.ModAddonModule;
import org.geysermc.floodgate.shared.module.ModListenerModule;
import org.geysermc.floodgate.shared.module.ModPlatformModule;
import org.incendo.cloud.CommandManager;

import java.nio.file.Path;

public abstract class FloodgateMod {

    private boolean started;
    private FloodgatePlatform platform;
    private Injector injector;

    @Getter @Setter
    private static CommandManager<UserAudience> commandManager;

    protected void init(ServerCommonModule commonModule, ModPlatformModule platformModule) {
        ModInjector.setInstance(new ModInjector());

        injector = Guice.createInjector(
                commonModule,
                platformModule
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
