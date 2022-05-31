package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import me.mjk134.sigma.ProjectSigma;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class SwapTeamsCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getArgument("player", ServerPlayerEntity.class);
        Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
        ProjectSigma.configManager.swap(player, scoreboard.getPlayerTeam(player.getEntityName()), scoreboard,  Objects.requireNonNull(scoreboard.getPlayerTeam(player.getEntityName())).getName(), context.getSource().getWorld());
        return 1;
    }

}
