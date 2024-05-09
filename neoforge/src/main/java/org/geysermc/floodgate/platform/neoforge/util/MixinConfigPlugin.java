package org.geysermc.floodgate.platform.neoforge.util;

import net.neoforged.fml.loading.LoadingModList;
import org.geysermc.floodgate.shared.util.ModMixinConfigPlugin;

public class MixinConfigPlugin extends ModMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals(" org.geysermc.floodgate.shared.mixin.ClientIntentionPacketMixin")) {
            return false; // TODO verify
        }
        if (mixinClassName.equals("org.geysermc.floodgate.shared.mixin.GeyserModInjectorMixin")) {
            return LoadingModList.get().getModFileById("geyser_neoforge") != null;
        }
        return true;
    }
}
