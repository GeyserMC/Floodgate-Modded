package org.geysermc.floodgate.shared.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class MixinConfigPluginImpl {

    private static boolean isGeyserLoaded() {
        return FabricLoader.getInstance().isModLoaded("geyser-fabric");
    }

    private static boolean applyProxyFix() {
        return FabricLoader.getInstance().isModLoaded("fabricproxy-lite");
    }
}
