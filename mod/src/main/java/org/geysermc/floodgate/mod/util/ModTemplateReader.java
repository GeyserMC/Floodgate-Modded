package org.geysermc.floodgate.mod.util;

import org.geysermc.configutils.file.template.TemplateReader;
import org.geysermc.floodgate.mod.FloodgateMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ModTemplateReader implements TemplateReader {

    @Override
    public BufferedReader read(String configName) {
        try {
            return new BufferedReader(new InputStreamReader(FloodgateMod.INSTANCE.resourceStream(configName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
