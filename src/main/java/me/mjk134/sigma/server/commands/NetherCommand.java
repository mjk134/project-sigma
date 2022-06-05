package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.server.ConfigManager;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import xyz.nucleoid.fantasy.Fantasy;

import java.util.Objects;

public class NetherCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) {
        if (!ConfigManager.ENABLED_NETHER) {
            context.getSource().sendError(new LiteralText("The nether is not active!"));
            return 1;
        }
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            context.getSource().sendFeedback(new LiteralText("Sending you to the nether.."), false);
            Team team = player.getScoreboard().getPlayerTeam(player.getEntityName());
            assert team != null;
            if (Objects.equals(team.getName(), ConfigManager.teamAName)) {
                context.getSource().getServer().getWorldRegistryKeys().forEach(r -> {
                    ServerWorld world = context.getSource().getServer().getWorld(r);
                    if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamAName.toLowerCase() + "_nether"))) {
                        assert world != null;
                        BlockPos spawn = world.getSpawnPos();
                        TeleportTarget teleportTarget = new TeleportTarget(new Vec3d(spawn.getX(), spawn.getY(), spawn.getZ()), new Vec3d(1, 1, 1), 0f, 0f);
                        FabricDimensionInternals.changeDimension(player, world, teleportTarget);
                    }
                });
            } else {
                context.getSource().getServer().getWorldRegistryKeys().forEach(r -> {
                    ServerWorld world = context.getSource().getServer().getWorld(r);
                    if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamBName.toLowerCase() + "_nether"))) {
                        assert world != null;
                        BlockPos spawn = world.getSpawnPos();
                        TeleportTarget teleportTarget = new TeleportTarget(new Vec3d(spawn.getX(), spawn.getY(), spawn.getZ()), new Vec3d(1, 1, 1), 0f, 0f);
                        FabricDimensionInternals.changeDimension(player, world, teleportTarget);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
