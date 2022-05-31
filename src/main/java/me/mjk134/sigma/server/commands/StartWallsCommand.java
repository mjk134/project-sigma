package me.mjk134.sigma.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.server.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.border.WorldBorder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class StartWallsCommand implements CommandInterface {

    public static int run(CommandContext<ServerCommandSource> context) {
        // TODO: Add automatic wall creation functionality
        if (ConfigManager.STARTED && ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("The game has already started!"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
            return 1;
        } else if (!ConfigManager.ENABLED) {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("You have to enable walls, you can do this by running: /walls enable"), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
            return 1;
        } else {
            try {
                context.getSource().getPlayer().sendMessage(new LiteralText("Starting game..").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);
            } catch (CommandSyntaxException exception) {
                return 1;
            }
        }
        ServerWorld serverWorld = context.getSource().getWorld();
        WorldBorder worldBorder = serverWorld.getWorldBorder();
        int worldSize = context.getArgument("size", Integer.class);
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(worldSize);
        worldBorder.setSafeZone(WorldBorder.DEFAULT_BORDER.getSafeZone());
        worldBorder.setDamagePerBlock(99999.0);
        worldBorder.setWarningBlocks(WorldBorder.DEFAULT_BORDER.getWarningBlocks());
        worldBorder.setWarningTime(WorldBorder.DEFAULT_BORDER.getWarningTime());
        // boolean isAboveLimit = worldSize * 383 * 2 > 32768;
        // Iterator<BlockPos> blockPosIterator = BlockPos.iterate(-worldSize /2, -64,0,worldSize/2,319,0).iterator();
        // List<BlockPos> list = Lists.newArrayList();
        //  for (int i = -worldSize / 2; i < worldSize / 2; i++) {
        //      for (int j = -64; j < 319; j++) {
        //           CompletableFuture<Void> v =  context.getSource().getServer().submit(new BlockStateTaskSubmit(i,j, serverWorld));
        //           while (!v.isDone()) {
        //           }
        //      }
        //  }
        //  ProjectSigma.LOGGER.debug("test iterable", blockPosIterator);
        //  blockPosIterator.forEach(blockPos -> {
        //      context.getSource().getServer().submit(() -> {
        //          serverWorld.setBlockState(blockPos, Blocks.BEDROCK.getDefaultState());
        //          ProjectSigma.LOGGER.debug("Set block");
        //      });
        //  });

        // while (blockPosIterator.hasNext()) {
        //     BlockPos blockPos;
        //     while(blockPosIterator.hasNext()) {
        //         blockPos = (BlockPos)blockPosIterator.next();
        //         Block block2 = serverWorld.getBlockState(blockPos).getBlock();
        //         serverWorld.updateNeighbors(blockPos, block2);
        //     }
        // }

        Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
        Team team1 = scoreboard.addTeam(ConfigManager.teamAName);
        team1.setDisplayName(new LiteralText(ConfigManager.teamAName));
        team1.setColor(Formatting.AQUA);
        team1.setFriendlyFireAllowed(false);
        team1.setShowFriendlyInvisibles(true);

        Team team2 = scoreboard.addTeam(ConfigManager.teamBName);
        team2.setDisplayName(new LiteralText(ConfigManager.teamBName));
        team2.setColor(Formatting.GOLD);
        team2.setFriendlyFireAllowed(false);
        team2.setShowFriendlyInvisibles(true);

        try {
            ProjectSigma.configManager.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 1;
    }

}
