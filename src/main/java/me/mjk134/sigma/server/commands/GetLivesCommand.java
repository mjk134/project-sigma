package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class GetLivesCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        if (!ConfigManager.STARTED || !ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The walls have not been activated yet!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        } else {
            try {
                ServerPlayerEntity player = (ServerPlayerEntity) EntityArgumentType.getEntity(context, "player");
                int numLives =  LivesManager.playerLives.get(player.getEntityName()).getNumLives();
                context.getSource().sendFeedback(new LiteralText(player.getEntityName() + " has " + numLives + (numLives == 1 ?" life left.":" lives left.")), false);
           } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

}
