package com.justindugan.juganmod.enchants;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;

public final class EnchantCapRules {
  public static final int CAP_ELYTRA = 2;
  public static final int CAP_SHIELD = 2;

  public static final int CAP_CROSSBOW = 3;
  public static final int CAP_BOW = 4;

  public static final int CAP_MACE = 4;
  public static final int CAP_TRIDENT = 4;
  public static final int CAP_SPEAR = 4;

  public static final int CAP_SWORD = 4;

  public static final int CAP_PICKAXE = 4;
  public static final int CAP_AXE_TOOL = 3;
  public static final int CAP_SHOVEL = 3;
  public static final int CAP_HOE = 3;

  public static final int CAP_ARMOR = 3;
  public static final int CAP_OTHER = 3;

  private EnchantCapRules() {
  }

  public static int getCap(ItemStack stack) {
    if (stack.is(Items.ENCHANTED_BOOK))
      return 0;

    if (stack.is(Items.ELYTRA))
      return CAP_ELYTRA;
    if (stack.is(Items.SHIELD))
      return CAP_SHIELD;

    if (stack.is(Items.CROSSBOW))
      return CAP_CROSSBOW;
    if (stack.is(Items.BOW))
      return CAP_BOW;

    if (stack.is(Items.MACE))
      return CAP_MACE;

    if (stack.is(ItemTags.SPEARS))
      return CAP_SPEAR;
    if (stack.is(Items.TRIDENT))
      return CAP_TRIDENT;

    if (stack.is(ItemTags.SWORDS))
      return CAP_SWORD;
    if (stack.is(ItemTags.AXES))
      return CAP_AXE_TOOL;
    if (stack.is(ItemTags.PICKAXES))
      return CAP_PICKAXE;
    if (stack.is(ItemTags.SHOVELS))
      return CAP_SHOVEL;
    if (stack.is(ItemTags.HOES))
      return CAP_HOE;

    if (stack.is(ItemTags.LEG_ARMOR) || stack.is(ItemTags.CHEST_ARMOR) || stack.is(ItemTags.FOOT_ARMOR)
        || stack.is(ItemTags.HEAD_ARMOR))
      return CAP_ARMOR;

    return CAP_OTHER;
  }

  public static int countItemEnchants(ItemStack stack, boolean countCurses) {
    ItemEnchantments ench = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
    if (countCurses)
      return ench.size();
    int n = 0;
    for (var e : ench.entrySet()) {
      if (!e.getKey().is(EnchantmentTags.CURSE))
        n++;
    }
    return n;
  }

  public static int countStoredEnchants(ItemStack stack, boolean countCurses) {
    ItemEnchantments ench = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
    if (countCurses)
      return ench.size();
    int n = 0;
    for (var e : ench.entrySet()) {
      if (!e.getKey().is(EnchantmentTags.CURSE))
        n++;
    }
    return n;
  }
}
