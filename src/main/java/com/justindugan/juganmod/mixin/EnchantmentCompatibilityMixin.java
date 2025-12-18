package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(Enchantment.class)
public abstract class EnchantmentCompatibilityMixin {

  @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
  private static void jugan$allowBaneWithDamageEnchants(
      Holder<Enchantment> a,
      Holder<Enchantment> b,
      CallbackInfoReturnable<Boolean> cir
  ) {
    boolean baneA = a.is(Enchantments.BANE_OF_ARTHROPODS);
    boolean baneB = b.is(Enchantments.BANE_OF_ARTHROPODS);

    if (baneA && (b.is(Enchantments.SHARPNESS) || b.is(Enchantments.SMITE))) {
      cir.setReturnValue(true);
      return;
    }
    if (baneB && (a.is(Enchantments.SHARPNESS) || a.is(Enchantments.SMITE))) {
      cir.setReturnValue(true);
    }
  }
}
