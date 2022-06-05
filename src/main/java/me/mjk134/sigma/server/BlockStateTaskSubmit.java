package me.mjk134.sigma.server;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BlockStateTaskSubmit implements Runnable {

    private final int xPos;
    private final int yPos;
    private final ServerWorld serverWorld;


    public BlockStateTaskSubmit(int xPos, int yPos, ServerWorld serverWorld) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.serverWorld = serverWorld;
    }

    @Override
    public void run() {
        serverWorld.setBlockState(new BlockPos(this.xPos, this.yPos, 0), Blocks.BEDROCK.getDefaultState());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
