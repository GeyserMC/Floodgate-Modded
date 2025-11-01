package org.geysermc.floodgate.platform.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.geysermc.floodgate.mod.util.ModTemplateReader;
import org.geysermc.floodgate.platform.neoforge.module.NeoForgeCommandModule;
import org.geysermc.floodgate.platform.neoforge.module.NeoForgePlatformModule;
import org.geysermc.floodgate.platform.neoforge.pluginmessage.NeoForgePluginMessageRegistration;
import org.geysermc.floodgate.platform.neoforge.util.TaskTimer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Mod("floodgate")
public final class NeoForgeFloodgateMod extends FloodgateMod {

    private final ModContainer container;

    public NeoForgeFloodgateMod(IEventBus modEventBus, ModContainer container) {
        this.container = container;
        init(
            new ServerCommonModule(
                FMLPaths.CONFIGDIR.get().resolve("floodgate"),
                new ModTemplateReader()
            ),
            new NeoForgePlatformModule(),
            new NeoForgeCommandModule()
        );

        modEventBus.addListener(this::onRegisterPackets);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        if (FMLLoader.getCurrent().getDist().isClient()) {
            NeoForge.EVENT_BUS.addListener(this::onClientStop);
        } else {
            NeoForge.EVENT_BUS.addListener(this::onServerStop);
        }
        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, TaskTimer.INSTANCE::onEndTick);
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

    private void onRegisterPackets(final RegisterPayloadHandlersEvent event) {
        // Set the registrar once we're given it - NeoForgePluginMessageRegistration was created earlier in NeoForgePlatformModule
        NeoForgePluginMessageRegistration pluginMessageRegistration = injector.getInstance(NeoForgePluginMessageRegistration.class);
        pluginMessageRegistration.setRegistrar(event.registrar("floodgate").optional());

        // We can now trigger the registering of our plugin message channels
        enable(new PluginMessageModule());
    }

    @Override
    public @NonNull InputStream resourceStream(String file) throws IOException {
        return Objects.requireNonNull(container.getModInfo().getOwningFile().getFile().getContents().openFile(file));
    }

    @Override
    public void schedule(Runnable runnable, int ticks) {
        TaskTimer.INSTANCE.runLater(runnable, ticks);
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getCurrent().getDist().isClient();
    }

}
