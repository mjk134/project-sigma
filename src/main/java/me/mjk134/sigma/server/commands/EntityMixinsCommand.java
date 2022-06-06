package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.IOException;

public class EntityMixinsCommand {

    public static int run(CommandContext<ServerCommandSource> context) {

        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 0;
            }
        }
        try {
            ProjectSigma.configManager.toggleEntityMixin();
            context.getSource().sendFeedback(new LiteralText("LivingEntityMixin functionality is now set to " + ConfigManager.LivingEntityMixin), true);
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }
}
