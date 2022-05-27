package me.mjk134.sigma.mixin;

import me.mjk134.sigma.ProjectSigma;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at=@At(value = "RETURN"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        try {
            ProjectSigma.livesManager.onDeath((ServerPlayerEntity) (Object) this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
