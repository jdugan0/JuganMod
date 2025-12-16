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

    // Minimal, explicit nerfs
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

    return vanillaMax;
  }
}