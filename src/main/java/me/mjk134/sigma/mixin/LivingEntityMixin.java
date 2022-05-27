package me.mjk134.sigma.mixin;

import me.mjk134.sigma.ProjectSigma;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onKilledBy", at = @At(value = "HEAD"))
    public void onAnimalDeath(LivingEntity entity, CallbackInfo ci) {
        try {
            if (entity != null) {
                ProjectSigma.LOGGER.info("Entity was killed by: " + entity.getEntityName());
                if ((LivingEntity) (Object) this instanceof AnimalEntity) {
                    ProjectSigma.LOGGER.info("I am an instance of animal entity");
                    if (entity instanceof ServerPlayerEntity) {
                        StatusEffect effect = StatusEffect.byRawId(18);
                        entity.addStatusEffect(new StatusEffectInstance(effect, 1200));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
