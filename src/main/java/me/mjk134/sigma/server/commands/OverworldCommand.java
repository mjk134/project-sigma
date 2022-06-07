package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.interfaces.ServerPlayerEntityInterface;
import me.mjk134.sigma.server.LivesManager;
import me.mjk134.sigma.server.Player;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class OverworldCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) {
        ServerWorld overWorld = context.getSource().getServer().getOverworld();
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player.getWorld().getRegistryKey() == World.OVERWORLD) {
                return 1;
            }
            context.getSource().sendFeedback(new LiteralText("Teleporting to overworld..."), false);
            // ((ServerPlayerEntityInterface)player).moveDimension(overWorld);
            // TeleportTarget teleportTarget = ((ServerPlayerEntityInterface)player).getTeleportLocation(overWorld);
            // assert teleportTarget != null;
            // player.teleport(overWorld, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
            Player actualPlayer = LivesManager.playerLives.get(player.getEntityName());
            actualPlayer.setLastNetherCoords(player.getPos());
            TeleportTarget teleportTarget;
            if (Objects.equals(actualPlayer.getLastNetherCoords(), new Vec3d(0, 0, 0))) {
                teleportTarget = new TeleportTarget(new Vec3d(Objects.requireNonNull(player.getSpawnPointPosition()).getX(), player.getSpawnPointPosition().getY(), player.getSpawnPointPosition().getZ()), new Vec3d(1, 1, 1), 0f, 0f);
            } else {
                teleportTarget = new TeleportTarget(actualPlayer.getLastOverworldCoords(), new Vec3d(1, 1, 1), 0f, 0f);
            }
            player.teleport(overWorld, teleportTarget.position.getX(), teleportTarget.position.getY(), teleportTarget.position.getZ(), teleportTarget.yaw, teleportTarget.pitch);
            LivesManager.playerLives.replace(player.getEntityName(), actualPlayer);
            // FabricDimensionInternals.changeDimension(player, overWorld, teleportTarget);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
