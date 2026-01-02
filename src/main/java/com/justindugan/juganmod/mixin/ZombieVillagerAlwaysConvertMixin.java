package com.justindugan.juganmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieVillagerAlwaysConvertMixin {

    @Mixin(Zombie.class)
    public abstract class ZombieMixin {

        @Redirect(method = "killedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z"))
        private boolean jugan$disableVillagerConversionChance(RandomSource random) {
            return false;
        }
    }

}
