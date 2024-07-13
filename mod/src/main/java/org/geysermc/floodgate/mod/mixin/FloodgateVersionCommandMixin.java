package org.geysermc.floodgate.mod.mixin;

import org.geysermc.floodgate.core.command.main.VersionSubcommand;
import org.geysermc.floodgate.core.player.UserAudience;
import org.incendo.cloud.context.CommandContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VersionSubcommand.class)
public class FloodgateVersionCommandMixin {

    @Redirect(method = "execute", at = @At(value = "HEAD"), remap = false)
    private void floodgate_modded$versionChecking(CommandContext<UserAudience> context) {
        UserAudience sender = context.sender();
        sender.sendMessage("Version checking is unfortunately not implemented yet. Sorry!");
    }
}
