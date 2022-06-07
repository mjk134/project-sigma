package me.mjk134.sigma.server;

import net.minecraft.util.math.Vec3d;

public class Player {

    public int numLives;
    public boolean allowsDonations;
    public Vec3d lastOverworldCoords = new Vec3d(0,0,0);
    public Vec3d lastNetherCoords = new Vec3d(0,0,0);

    public Vec3d getLastOverworldCoords() {
        return lastOverworldCoords;
    }

    public void setLastOverworldCoords(Vec3d lastOverworldCoords) {
        this.lastOverworldCoords = lastOverworldCoords;
    }

    public Vec3d getLastNetherCoords() {
        return lastNetherCoords;
    }

    public void setLastNetherCoords(Vec3d lastNetherCoords) {
        this.lastNetherCoords = lastNetherCoords;
    }

    public Player(int numLives, boolean allowsDonations) {
        this.numLives = numLives;
        this.allowsDonations = allowsDonations;
    }

    public int getNumLives() {
        return numLives;
    }

    public void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    public boolean isAllowsDonations() {
        return allowsDonations;
    }

    public void toggleAllowsDonations() {
        this.allowsDonations = !this.allowsDonations;
    }
}
