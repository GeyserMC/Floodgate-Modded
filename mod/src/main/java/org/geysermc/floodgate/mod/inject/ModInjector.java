package org.geysermc.floodgate.mod.inject;

import com.google.inject.Inject;
import io.netty.channel.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.core.inject.CommonPlatformInjector;

/**
 * Handles Floodgate injection logic for client-side Netty channels.
 * This version is streamlined for single-time injection with basic lifecycle handling.
 */
@RequiredArgsConstructor
public final class ModInjector extends CommonPlatformInjector {
    public static final ModInjector INSTANCE = new ModInjector();

    @Getter
    private final boolean injected = true;

    @Inject
    private FloodgateLogger logger;

    @Override
    public void inject() {
        // No-op (not used for this platform)
    }

    /**
     * Injects Floodgate into the Netty channel if not already injected.
     *
     * @param future the Netty ChannelFuture for the connection
     */
    public void injectClient(ChannelFuture future) {
        final String PIPELINE_NAME = "floodgate-init";
        Channel channel = future.channel();
        ChannelPipeline pipeline = channel.pipeline();

        if (pipeline.names().contains(PIPELINE_NAME)) {
            logger.debug("Floodgate injection attempted twice; skipping.");
            return;
        }

        pipeline.addFirst(PIPELINE_NAME, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(@NonNull ChannelHandlerContext ctx, @NonNull Object msg) throws Exception {
                super.channelRead(ctx, msg);

                if (!(msg instanceof Channel innerChannel)) return;

                innerChannel.pipeline().addLast(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(@NonNull Channel channel) {
                        injectAddonsCall(channel, false);
                        addInjectedClient(channel);

                        channel.closeFuture().addListener(listener -> {
                            channelClosedCall(channel);
                            removeInjectedClient(channel);
                        });
                    }
                });
            }
        });
    }

    @Override
    public void removeInjection() {
        // No-op (not required for this implementation)
    }
}
