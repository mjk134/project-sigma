package me.mjk134.sigma.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.mjk134.sigma.ProjectSigma;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.FileReader;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean isBaby();

    @Inject(method = "onKilledBy", at = @At(value = "HEAD"))
    public void onAnimalDeath(LivingEntity entity, CallbackInfo ci) {
        FileReader reader = null;
        try {
            reader = new FileReader("project-sigma.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        if (!json.get("livingEntityMixin").getAsBoolean()) return;
        try {
            if (entity != null) {
                if ((LivingEntity) (Object) this instanceof AnimalEntity) {
                    if (entity instanceof ServerPlayerEntity) {
                        StatusEffect effect = StatusEffect.byRawId(18);
                        entity.addStatusEffect(new StatusEffectInstance(effect, 1200));
                    }
                } else if ((LivingEntity) (Object) this instanceof VillagerEntity && this.isBaby()) {
                    VillagerEntity babyVillager = (VillagerEntity) (Object) this;
                    entity.getWorld().spawnEntity(new ExperienceOrbEntity(entity.getWorld(), babyVillager.getX(), babyVillager.getY(), babyVillager.getZ(), 5));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
