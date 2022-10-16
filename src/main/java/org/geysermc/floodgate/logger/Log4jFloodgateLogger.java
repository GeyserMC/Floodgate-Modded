package org.geysermc.floodgate.logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.util.LanguageManager;

import static org.geysermc.floodgate.util.MessageFormatter.format;

@Singleton
public final class Log4jFloodgateLogger implements FloodgateLogger {

    @Named("logger")
    @Inject
    private Logger logger;

    private LanguageManager languageManager;

    @Inject
    private void init(LanguageManager languageManager) {
        // LanguageManager requires the FloodgateLogger, which requires the LanguageManager
        // LanguageManager being injected specifically in init allows this circular dependency to work out
        // i.e. when the manager is being injected (for the logger which is being injected), it can call the logger
        this.languageManager = languageManager;
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        logger.error(format(message, args), throwable);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void translatedInfo(String message, Object... args) {
        logger.info(languageManager.getLogString(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void trace(String message, Object... args) {
        logger.trace(message, args);
    }

    @Override
    public boolean isDebug() {
        return logger.isDebugEnabled();
    }
}
