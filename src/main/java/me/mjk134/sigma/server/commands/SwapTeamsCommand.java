package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Objects;

public class SwapTeamsCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = (ServerPlayerEntity) EntityArgumentType.getEntity(context, "player");
        Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
        RegistryKey<World> registryKey = context.getSource().getServer().getOverworld().getRegistryKey();
        ProjectSigma.configManager.swap(player, scoreboard.getPlayerTeam(player.getEntityName()), scoreboard,  Objects.requireNonNull(scoreboard.getPlayerTeam(player.getEntityName())).getName(), context.getSource().getWorld());
        if (Objects.equals(scoreboard.getPlayerTeam(player.getEntityName()), scoreboard.getTeam(ConfigManager.teamAName))) {
            player.setSpawnPoint(registryKey, new BlockPos(1, 118, -7), 0.0f, true, false);
            player.teleport(1, 118, -7);
            context.getSource().sendFeedback(new LiteralText((player.getEntityName() + " has successfully switched to " + ConfigManager.teamAName)), true);
        }
        else {
            player.setSpawnPoint(registryKey, new BlockPos(0, 113, 18), 0.0f, true, false);
            player.teleport(0, 113,18);
            context.getSource().sendFeedback(new LiteralText((player.getEntityName() + " has successfully switched to " + ConfigManager.teamBName)), true);
        }
        return 1;
    }

}
