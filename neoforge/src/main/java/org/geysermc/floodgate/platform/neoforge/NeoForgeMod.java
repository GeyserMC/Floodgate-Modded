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
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.geysermc.floodgate.core.module.PluginMessageModule;
import org.geysermc.floodgate.core.module.ServerCommonModule;
import org.geysermc.floodgate.platform.neoforge.module.NeoForgeCommandModule;
import org.geysermc.floodgate.platform.neoforge.module.NeoForgePlatformModule;
import org.geysermc.floodgate.platform.neoforge.pluginmessage.NeoForgePluginMessageRegistration;
import org.geysermc.floodgate.platform.neoforge.util.NeoForgeTemplateReader;
import org.geysermc.floodgate.shared.FloodgateMod;

@Mod("floodgate")
public final class NeoForgeMod extends FloodgateMod {

    public NeoForgeMod(IEventBus modEventBus, ModContainer container) {
        init(
            new ServerCommonModule(
                FMLPaths.CONFIGDIR.get().resolve("floodgate"),
                new NeoForgeTemplateReader(container)
            ),
            new NeoForgePlatformModule(),
            new NeoForgeCommandModule()
        );

        modEventBus.addListener(this::onRegisterPackets);
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

    private void onRegisterPackets(final RegisterPayloadHandlersEvent event) {
        NeoForgePluginMessageRegistration.setRegistrar(event.registrar("floodgate").optional());
        enable(new PluginMessageModule());
    }
}
