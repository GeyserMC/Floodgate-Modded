package org.geysermc.floodgate.mod.mixin;

import org.geysermc.floodgate.core.util.Utils;
import org.geysermc.floodgate.mod.FloodgateMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mixes into Floodgate's {@link Utils} class to override resource loading behavior.
 * This prevents shared classloader conflicts when mods like Geyser and Floodgate coexist.
 */
@Mixin(value = Utils.class, remap = false)
public class FloodgateUtilMixin {

    @Redirect(
        method = "readProperties",
        at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;")
    )
    private static InputStream redirectReadProperties(ClassLoader loader, String pathStr) {
        return floodgate$resolveInputStream(pathStr, false);
    }

    @Redirect(
        method = "getGeneratedClassesForAnnotation(Ljava/lang/String;)Ljava/util/Set;",
        at = @At(value = "INVOKE", target = "Ljava/lang/ClassLoader;getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;")
    )
    private static InputStream redirectGeneratedClasses(ClassLoader loader, String pathStr) {
        return floodgate$resolveInputStream(pathStr, true);
    }

    /**
     * Resolves a resource path to an InputStream using Floodgate's custom method.
     *
     * @param pathStr        the resource path
     * @param failIfMissing  whether to throw if the resource path was not found
     * @return an InputStream or null
     */
    @Unique
    private static InputStream floodgate$resolveInputStream(String pathStr, boolean failIfMissing) {
        Path path = FloodgateMod.INSTANCE.resourcePath(pathStr);
        if (path == null) {
            if (failIfMissing) {
                throw new IllegalStateException("Failed to find resource: " + pathStr);
            }
            return null;
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource: " + pathStr, e);
        }
    }
}
