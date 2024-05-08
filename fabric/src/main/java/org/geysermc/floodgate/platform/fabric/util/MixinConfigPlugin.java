package org.geysermc.floodgate.platform.fabric.util;

import net.fabricmc.loader.api.FabricLoader;
import org.geysermc.floodgate.util.ModMixinConfigPlugin;

public class MixinConfigPlugin extends ModMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("org.geysermc.floodgate.mixin.ClientIntentionPacketMixin")) {
            //returns true if fabricproxy-lite is present, therefore loading the mixin. If not present, the mixin will not be loaded.
            return FabricLoader.getInstance().isModLoaded("fabricproxy-lite");
        }
        if (mixinClassName.equals("org.geysermc.floodgate.mixin.GeyserModInjectorMixin")) {
            return FabricLoader.getInstance().isModLoaded("geyser-fabric");
        }
        return true;
    }
}
