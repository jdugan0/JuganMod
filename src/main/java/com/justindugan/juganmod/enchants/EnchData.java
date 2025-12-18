package com.justindugan.juganmod.enchants;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchData {
  public final ResourceKey<Enchantment> key;
  public final int level;
  public final int maxLevel;

  public EnchData(ResourceKey<Enchantment> key, int level, int maxLevel) {
    this.key = key;
    this.level = level;
    this.maxLevel = maxLevel;
  }
}
