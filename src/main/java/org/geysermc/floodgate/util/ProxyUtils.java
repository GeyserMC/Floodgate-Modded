package org.geysermc.floodgate.util;

import net.fabricmc.loader.api.FabricLoader;

public class ProxyUtils {
    public static boolean isProxyData() {
        return FabricLoader.getInstance().isModLoaded("fabricproxy-lite");
    }
}
