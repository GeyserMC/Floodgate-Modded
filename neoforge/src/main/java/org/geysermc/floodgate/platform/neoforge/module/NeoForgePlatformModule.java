package org.geysermc.floodgate.platform.neoforge.module;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.geysermc.floodgate.listener.ModEventListener;
import org.geysermc.floodgate.module.ModPlatformModule;
import org.geysermc.floodgate.platform.listener.ListenerRegistration;
import org.geysermc.floodgate.platform.neoforge.listener.NeoForgeEventRegistration;
import org.geysermc.floodgate.platform.neoforge.pluginmessage.NeoForgePluginMessageRegistration;
import org.geysermc.floodgate.platform.neoforge.pluginmessage.NeoForgePluginMessageUtils;
import org.geysermc.floodgate.platform.pluginmessage.PluginMessageUtils;
import org.geysermc.floodgate.pluginmessage.PluginMessageRegistration;

public class NeoForgePlatformModule extends ModPlatformModule {

    @Provides
    @Singleton
    public ListenerRegistration<ModEventListener> listenerRegistration() {
        return new NeoForgeEventRegistration();
    }

    @Provides
    @Singleton
    public PluginMessageUtils pluginMessageUtils() {
        return new NeoForgePluginMessageUtils();
    }

    @Provides
    @Singleton
    public PluginMessageRegistration pluginMessageRegister() {
        return new NeoForgePluginMessageRegistration();
    }

    @Provides
    @Named("implementationName")
    public String implementationName() {
        return "NeoForge";
    }

}
