package org.geysermc.floodgate.shared.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.geysermc.floodgate.shared.listener.ModEventListener;
import org.geysermc.floodgate.core.register.ListenerRegister;

public final class ModListenerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ListenerRegister<ModEventListener>>() {}).asEagerSingleton();
    }

    @Singleton
    @ProvidesIntoSet
    public ModEventListener modEventListener() {
        return new ModEventListener();
    }
}
