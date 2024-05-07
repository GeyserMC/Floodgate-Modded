package org.geysermc.floodgate.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.geysermc.floodgate.listener.ModEventListener;
import org.geysermc.floodgate.register.ListenerRegister;

public final class ModListenerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ListenerRegister<ModEventListener>>() {}).asEagerSingleton();
    }

    @Singleton
    @ProvidesIntoSet
    public ModEventListener fabricEventListener() {
        return new ModEventListener();
    }
}
