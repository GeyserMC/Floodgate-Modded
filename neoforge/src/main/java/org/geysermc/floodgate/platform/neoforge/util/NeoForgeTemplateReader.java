package org.geysermc.floodgate.platform.neoforge.util;

import net.neoforged.fml.ModContainer;
import org.geysermc.configutils.file.template.TemplateReader;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class NeoForgeTemplateReader implements TemplateReader {

    private final ModContainer container;

    public NeoForgeTemplateReader(ModContainer container) {
        this.container = container;
    }

    @Override
    public BufferedReader read(String configName) {
        try {
            Path path = container.getModInfo().getOwningFile().getFile().findResource(configName);
            return Files.newBufferedReader(path);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
