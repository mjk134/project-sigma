package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.interfaces.ServerPlayerEntityInterface;
import me.mjk134.sigma.server.ConfigManager;
import me.mjk134.sigma.server.LivesManager;
import me.mjk134.sigma.server.Player;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class NetherCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) {
        if (!ConfigManager.ENABLED_NETHER) {
            context.getSource().sendError(new LiteralText("The nether is not active!"));
            return 1;
        }
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player.getWorld().getRegistryKey() == ProjectSigma.teamANether.getRegistryKey() || player.getWorld().getRegistryKey() == ProjectSigma.teamBNether.getRegistryKey()) {
                return 1;
            }
            Player actualPlayer = LivesManager.playerLives.get(player.getEntityName());
            context.getSource().sendFeedback(new LiteralText("Sending you to the nether.."), false);
            Team team = player.getScoreboard().getPlayerTeam(player.getEntityName());
            assert team != null;
            if (Objects.equals(team.getName(), ConfigManager.teamAName)) {
                context.getSource().getServer().getWorldRegistryKeys().forEach(r -> {
                    ServerWorld world = context.getSource().getServer().getWorld(r);
                    if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamAName.toLowerCase() + "_nether"))) {
                        assert world != null;
                        BlockPos spawn = world.getSpawnPos();
                        actualPlayer.setLastOverworldCoords(player.getPos());
                        TeleportTarget teleportTarget;
                        if (Objects.equals(actualPlayer.getLastNetherCoords(), new Vec3d(0, 0, 0))) {
                            teleportTarget = new TeleportTarget(new Vec3d(spawn.getX(), spawn.getY(), spawn.getZ()), new Vec3d(1, 1, 1), 0f, 0f);
                        } else {
                            teleportTarget = new TeleportTarget(actualPlayer.getLastNetherCoords(), new Vec3d(1, 1, 1), 0f, 0f);
                        }
                        player.teleport(world, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
                        LivesManager.playerLives.replace(player.getEntityName(), actualPlayer);
                        // TeleportTarget teleportTarget = ((ServerPlayerEntityInterface)player).getTeleportLocation(world);
                        // assert teleportTarget != null;
                        // player.getSpawnPointPosition();
                        // // player.getWorld().getSpawnPos()
                        // player.teleport(world, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
                        // ((ServerPlayerEntityInterface)player).moveDimension(world);
                        // player.moveToWorld(world);
                        // FabricDimensionInternals.changeDimension(player, world, teleportTarget);
                    }
                });
            } else {
                context.getSource().getServer().getWorldRegistryKeys().forEach(r -> {
                    ServerWorld world = context.getSource().getServer().getWorld(r);
                    if (Objects.equals(r.getValue(), new Identifier("project-sigma", ConfigManager.teamBName.toLowerCase() + "_nether"))) {
                        assert world != null;
                        BlockPos spawn = world.getSpawnPos();
                        actualPlayer.setLastOverworldCoords(player.getPos());
                        TeleportTarget teleportTarget;
                        if (Objects.equals(actualPlayer.getLastNetherCoords(), new Vec3d(0, 0, 0))) {
                            teleportTarget = new TeleportTarget(new Vec3d(spawn.getX(), spawn.getY(), spawn.getZ()), new Vec3d(1, 1, 1), 0f, 0f);
                        } else {
                            teleportTarget = new TeleportTarget(actualPlayer.getLastNetherCoords(), new Vec3d(1, 1, 1), 0f, 0f);
                        }
                        LivesManager.playerLives.replace(player.getEntityName(), actualPlayer);
                        player.teleport(world, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
                        // FabricDimensionInternals.changeDimension(player, world, teleportTarget);
                        // ((ServerPlayerEntityInterface)player).moveDimension(world);
                        // ServerPlayerEntity newPlayer = (ServerPlayerEntity) player.moveToWorld(world);
                        // player.kill();
                        // TeleportTarget teleportTarget = ((ServerPlayerEntityInterface)player).getTeleportLocation(world);
                        // player.teleport(world, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
