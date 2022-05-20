package me.mjk134.sigma.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.border.WorldBorder;

public class StartWallsCommand {

    public static int run(CommandContext<ServerCommandSource> context) {
        // TODO: Add automatic wall creation functionality
        try {
            context.getSource().getPlayer().sendMessage(new LiteralText("Creating new walls, this may take a while"), false);
        } catch (CommandSyntaxException exception) {
            return 1;
        }
        ServerWorld serverWorld = context.getSource().getWorld();
        WorldBorder worldBorder = serverWorld.getWorldBorder();
        int worldSize = context.getArgument("size", Integer.class);
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(worldSize);
        worldBorder.setSafeZone(WorldBorder.DEFAULT_BORDER.getSafeZone());
        worldBorder.setDamagePerBlock(WorldBorder.DEFAULT_BORDER.getDamagePerBlock());
        worldBorder.setWarningBlocks(WorldBorder.DEFAULT_BORDER.getWarningBlocks());
        worldBorder.setWarningTime(WorldBorder.DEFAULT_BORDER.getWarningTime());
       //boolean isAboveLimit = worldSize * 383 * 2 > 32768;
       //try {
       //    for (int i = -worldSize; i < worldSize; i++) {
       //        int j;
       //        for (j = -64; j < 319; j++) {
       //            serverWorld.setBlockState(new BlockPos(i, j, 0), Blocks.BEDROCK.getDefaultState());
       //            Thread.sleep(100);
       //        }
       //        for (j = -64; j < 319; j++) {
       //            serverWorld.setBlockState(new BlockPos(0, j, i), Blocks.BEDROCK.getDefaultState());
       //            Thread.sleep(100);
       //        }
       //    }
       //} catch (InterruptedException ex) {
       //    return 1;
       //}
        return 1;
    }

}
