package com.justindugan.juganmod.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.enchants.xp.XpBonusContext;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

@Mixin(LivingEntity.class)
public abstract class ScholarEntityContextMixin {

    @Inject(method = "dropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void jugan$push(ServerLevel level, @Nullable Entity killer, CallbackInfo ci) {
        ServerPlayer sp = null;

        if (killer instanceof ServerPlayer p) {
            sp = p;
        } else if (killer instanceof Projectile proj && proj.getOwner() instanceof ServerPlayer p) {
            sp = p;
        }

        if (sp != null) {
            XpBonusContext.push(sp, sp.getMainHandItem());
        }
    }

    @Inject(method = "dropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    private void jugan$pop(ServerLevel level, @Nullable Entity killer, CallbackInfo ci) {
        XpBonusContext.pop();
    }
}
