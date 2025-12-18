package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@Mixin(Enchantment.class)
public abstract class ProtectionStackingCompatibilityMixin {

  @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
  private static void jugan$protAndFeatherRules(
      Holder<Enchantment> a,
      Holder<Enchantment> b,
      CallbackInfoReturnable<Boolean> cir) {
    if (isFeather(a) && isElementalSpecialty(b)) {
      cir.setReturnValue(false);
      return;
    }
    if (isFeather(b) && isElementalSpecialty(a)) {
      cir.setReturnValue(false);
      return;
    }
    if (isGeneralProtection(a) && isAnySpecialty(b)) {
      cir.setReturnValue(true);
      return;
    }
    if (isGeneralProtection(b) && isAnySpecialty(a)) {
      cir.setReturnValue(true);
    }
  }

  private static boolean isGeneralProtection(Holder<Enchantment> e) {
    return e.is(Enchantments.PROTECTION);
  }

  private static boolean isFeather(Holder<Enchantment> e) {
    return e.is(Enchantments.FEATHER_FALLING);
  }

  private static boolean isElementalSpecialty(Holder<Enchantment> e) {
    return e.is(Enchantments.FIRE_PROTECTION)
        || e.is(Enchantments.BLAST_PROTECTION)
        || e.is(Enchantments.PROJECTILE_PROTECTION);
  }

  private static boolean isAnySpecialty(Holder<Enchantment> e) {
    return isElementalSpecialty(e) || isFeather(e);
  }
}
