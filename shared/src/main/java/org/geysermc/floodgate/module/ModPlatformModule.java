package org.geysermc.floodgate.module;

import com.google.inject.name.Names;
import org.apache.logging.log4j.Logger;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.inject.ModInjector;
import org.geysermc.floodgate.logger.Log4jFloodgateLogger;
import org.geysermc.floodgate.platform.command.CommandUtil;
import org.geysermc.floodgate.platform.util.PlatformUtils;
import org.geysermc.floodgate.pluginmessage.ModSkinApplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.inject.CommonPlatformInjector;
import org.geysermc.floodgate.skin.SkinApplier;
import org.geysermc.floodgate.util.LanguageManager;
import org.geysermc.floodgate.util.ModCommandUtil;
import org.geysermc.floodgate.util.ModPlatformUtils;

@RequiredArgsConstructor
public abstract class ModPlatformModule extends AbstractModule {

    @Provides
    @Singleton
    public CommandUtil commandUtil(
            FloodgateApi api,
            FloodgateLogger logger,
            LanguageManager languageManager) {
        return new ModCommandUtil(languageManager, api, logger);
    }

    @Override
    protected void configure() {
        bind(PlatformUtils.class).to(ModPlatformUtils.class);
        bind(Logger.class).annotatedWith(Names.named("logger")).toInstance(LogManager.getLogger("floodgate"));
        bind(FloodgateLogger.class).to(Log4jFloodgateLogger.class);
    }

    /*
    DebugAddon / PlatformInjector
     */

    @Provides
    @Singleton
    public CommonPlatformInjector platformInjector() {
        return ModInjector.getInstance();
    }

    @Provides
    @Named("packetEncoder")
    public String packetEncoder() {
        return "encoder";
    }

    @Provides
    @Named("packetDecoder")
    public String packetDecoder() {
        return "decoder";
    }

    @Provides
    @Named("packetHandler")
    public String packetHandler() {
        return "packet_handler";
    }

    @Provides
    @Singleton
    public SkinApplier skinApplier() {
        return new ModSkinApplier();
    }
}
