package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.Objects;

public class SwapTeamsCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity player = EntityArgumentType.getEntity(context, "player");

        Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
        ProjectSigma.configManager.swap((ServerPlayerEntity) player, scoreboard.getPlayerTeam(player.getEntityName()), scoreboard,  Objects.requireNonNull(scoreboard.getPlayerTeam(player.getEntityName())).getName(), context.getSource().getWorld());
        if (Objects.equals(scoreboard.getPlayerTeam(player.getEntityName()), scoreboard.getTeam(ConfigManager.teamAName))) {
            context.getSource().sendFeedback(new LiteralText((player.getEntityName() + " has successfully switched to " + ConfigManager.teamAName)), true);
        }
        else {
            context.getSource().sendFeedback(new LiteralText((player.getEntityName() + " has successfully switched to " + ConfigManager.teamBName)), true);
        }

        return 1;
    }

}
