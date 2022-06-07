package me.mjk134.sigma.interfaces;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

public interface ServerPlayerEntityInterface {

    void moveDimension(ServerWorld destination);

    TeleportTarget getTeleportLocation(ServerWorld destination);

}
