package com.justindugan.juganmod.enchants;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public final class EnchantTableRules {
  private EnchantTableRules() {
  }

  public static int maxTableLevel(Holder<Enchantment> enchantment) {
    int vanillaMax = enchantment.value().getMaxLevel();

    if (enchantment.is(Enchantments.SHARPNESS))
      return Math.min(vanillaMax, 4);

    if (enchantment.is(Enchantments.PROTECTION))
      return Math.min(vanillaMax, 3);

    if (enchantment.is(Enchantments.EFFICIENCY))
      return Math.min(vanillaMax, 4);

    if (enchantment.is(Enchantments.FORTUNE))
      return Math.min(vanillaMax, 2);

    if (enchantment.is(Enchantments.POWER))
      return Math.min(vanillaMax, 4);

    if (enchantment.is(Enchantments.UNBREAKING))
      return Math.min(vanillaMax, 2);

    if (enchantment.is(Enchantments.LOOTING))
      return Math.min(vanillaMax, 2);

    if (enchantment.is(Enchantments.SWEEPING_EDGE))
      return Math.min(vanillaMax, 2);

    if (enchantment.is(Enchantments.KNOCKBACK))
      return Math.min(vanillaMax, 1);

    if (enchantment.is(Enchantments.FIRE_ASPECT))
      return Math.min(vanillaMax, 1);

    if (enchantment.is(Enchantments.QUICK_CHARGE))
      return Math.min(vanillaMax, 2);

    if (enchantment.is(Enchantments.FEATHER_FALLING))
      return Math.min(vanillaMax, 3);

    return vanillaMax;
  }
}