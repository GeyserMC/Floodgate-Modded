package org.geysermc.floodgate.platform.neoforge.util;

import net.neoforged.fml.ModList;
import org.geysermc.floodgate.shared.util.ModMixinConfigPlugin;

public class MixinConfigPlugin extends ModMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("org.geysermc.floodgate.mixin.ClientIntentionPacketMixin")) {
            return false; // TODO verify
        }
        if (mixinClassName.equals("org.geysermc.floodgate.mixin.GeyserModInjectorMixin")) {
            return ModList.get().isLoaded("geyser-neoforge");
        }
        return true;
    }
}
