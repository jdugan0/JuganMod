package com.justindugan.juganmod.mixin;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(Enchantment.class)
public abstract class BaneDamageScaleMixin {
  private static final float BANE_DAMAGE_SCALE = 0.6f;

  @Unique
  private static final ThreadLocal<Float> jugan$preDamage = ThreadLocal.withInitial(() -> 0.0f);

  @Unique
  private boolean jugan$isBane(ServerLevel level) {
    Holder.Reference<Enchantment> bane = level.registryAccess()
        .lookupOrThrow(Registries.ENCHANTMENT)
        .getOrThrow(Enchantments.BANE_OF_ARTHROPODS);

    return bane.value() == (Object) this;
  }

  @Inject(
      method = "modifyDamage(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lorg/apache/commons/lang3/mutable/MutableFloat;)V",
      at = @At("HEAD")
  )
  private void jugan$baneDamageStoreBefore(
      ServerLevel level, int enchLevel, ItemStack stack, Entity target, DamageSource source, MutableFloat damage,
      CallbackInfo ci
  ) {
    if (jugan$isBane(level)) {
      jugan$preDamage.set(damage.floatValue());
    }
  }

  @Inject(
      method = "modifyDamage(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lorg/apache/commons/lang3/mutable/MutableFloat;)V",
      at = @At("RETURN")
  )
  private void jugan$baneDamageScaleDelta(
      ServerLevel level, int enchLevel, ItemStack stack, Entity target, DamageSource source, MutableFloat damage,
      CallbackInfo ci
  ) {
    if (!jugan$isBane(level)) return;

    float before = jugan$preDamage.get();
    float after = damage.floatValue();
    float delta = after - before;

    damage.setValue(before + delta * BANE_DAMAGE_SCALE);
  }
}
