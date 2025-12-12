package org.geysermc.floodgate.mod.util.neoforge;

import net.neoforged.fml.loading.FMLLoader;

public class ModMixinConfigPluginImpl {
    public static boolean isGeyserLoaded() {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById("geyser_neoforge") != null;
    }

    public static boolean applyProxyFix() {
        return false;
    }
}
