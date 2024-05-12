package org.geysermc.floodgate.shared.mixin;

import org.geysermc.floodgate.core.util.Utils;
import org.geysermc.floodgate.shared.FloodgateMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(value = Utils.class, remap = false)
public class FloodgateUtilMixin {
    @Redirect(method = "readProperties",
            at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    private static InputStream redirectInputStream(ClassLoader instance, String string) {
        Path path = FloodgateMod.INSTANCE.getResourcePath(string);
        try {
            return path == null ? null : Files.newInputStream(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Redirect(method = "getGeneratedClassesForAnnotation(Ljava/lang/String;)Ljava/util/Set;",
            at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;"))
    private static InputStream redirectInputStreamAnnotation(ClassLoader instance, String string) {
        Path path = FloodgateMod.INSTANCE.getResourcePath(string);
        try {
            return path == null ? null : Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
