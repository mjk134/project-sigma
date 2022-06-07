package me.mjk134.sigma.mixin;

import me.mjk134.sigma.ProjectSigma;
import me.mjk134.sigma.interfaces.ServerPlayerEntityInterface;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityInterface {
    @Shadow
    protected abstract TeleportTarget getTeleportTarget(ServerWorld destination);

    @Inject(method = "onDeath", at=@At(value = "RETURN"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        try {
            ProjectSigma.livesManager.onDeath((ServerPlayerEntity) (Object) this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveDimension(ServerWorld destination) {
        FabricDimensionInternals.changeDimension((ServerPlayerEntity) (Object) this, destination, getTeleportTarget(destination));
    }

    @Override
    public TeleportTarget getTeleportLocation(ServerWorld destination) {
        return this.getTeleportTarget(destination);
    }
}
