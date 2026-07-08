package org.geysermc.floodgate.mod.mixin;

import org.geysermc.floodgate.core.util.LanguageManager;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.net.URL;

@Mixin(value = LanguageManager.class, remap = false)
public class LanguageManagerMixin {
    @Redirect(method = "isValidLanguage",
        at = @At(value = "INVOKE", target = "Ljava/lang/Class;getResource(Ljava/lang/String;)Ljava/net/URL;"))
    private static URL floodgate$redirectUrl(Class<?> instance, String string) {
        try {
            return FloodgateMod.INSTANCE.resourceUrl(string);
        } catch (IOException e) {
            return null; // Likely doesn't exist, if it's something else, fallback should still work
        }
    }
}
