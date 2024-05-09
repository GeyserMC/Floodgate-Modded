package org.geysermc.floodgate.platform.neoforge.util;

import org.geysermc.configutils.file.template.TemplateReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NeoForgeTemplateReader implements TemplateReader {
    @Override
    public BufferedReader read(String configName) {
        try {
            InputStream stream = getClass().getResourceAsStream(configName);
            Objects.requireNonNull(stream, "config stream cannot be null!");
            System.out.println("found config called: " + configName);
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
