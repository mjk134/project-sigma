package me.mjk134.sigma.server;

import net.minecraft.util.math.Vec3d;

public class Player {

    public int numLives;
    public boolean allowsDonations;

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

    public void setAllowsDonations(boolean allowsDonations) {
        this.allowsDonations = allowsDonations;
    }
}
