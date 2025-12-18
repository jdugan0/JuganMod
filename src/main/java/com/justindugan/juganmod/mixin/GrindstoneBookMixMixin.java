package com.justindugan.juganmod.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.enchants.EnchData;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneBookMixMixin {

  @Shadow
  @Final
  private Container repairSlots;
  @Shadow
  @Final
  private Container resultSlots;
  @Shadow
  @Final
  private ContainerLevelAccess access;

  @Unique
  private static final Map<EnchantPair, ResourceKey<Enchantment>> MIX = buildMixMap();

  @Inject(method = "createResult", at = @At("TAIL"))
  private void jugan$mixBooks(CallbackInfo ci) {
    ItemStack a = repairSlots.getItem(0);
    ItemStack b = repairSlots.getItem(1);
    if (!isSingleEnchantedBook(a) || !isSingleEnchantedBook(b))
      return;

    EnchData ea = readSingleStoredEnchant(a);
    EnchData eb = readSingleStoredEnchant(b);
    if (ea == null || eb == null)
      return;

    ResourceKey<Enchantment> outKey = MIX.get(new EnchantPair(ea.key, eb.key));
    if (outKey == null)
      return;

    final Holder.Reference<Enchantment>[] outRef = new Holder.Reference[1];
    this.access.execute((level, pos) -> {
      outRef[0] = level.registryAccess()
          .lookupOrThrow(Registries.ENCHANTMENT)
          .getOrThrow(outKey);
    });
    Holder.Reference<Enchantment> out = outRef[0];
    if (out == null)
      return;

    int outMax = out.value().getMaxLevel();
    if (outMax <= 0)
      return;

    float na = (float) ea.level / (float) ea.maxLevel;
    float nb = (float) eb.level / (float) eb.maxLevel;
    float normalized = (na + nb) * 0.5f;
    if (outKey.equals(ea.key) || outKey.equals(eb.key))
      normalized *= 0.85f;

    int outLevel = clamp(Math.round(normalized * outMax), 1, outMax);

    ItemStack outBook = EnchantmentHelper.createBook(new EnchantmentInstance(out, outLevel));

    resultSlots.setItem(0, outBook);
  }

  @Unique
  private static boolean isSingleEnchantedBook(ItemStack s) {
    if (!s.is(Items.ENCHANTED_BOOK))
      return false;
    ItemEnchantments stored = s.get(DataComponents.STORED_ENCHANTMENTS);
    if (stored == null || stored.isEmpty())
      return false;
    int c = 0;
    for (@SuppressWarnings("unused")
    Object2IntMap.Entry<Holder<Enchantment>> e : stored.entrySet())
      c++;
    return c == 1;
  }

  @Unique
  private static EnchData readSingleStoredEnchant(ItemStack s) {
    ItemEnchantments stored = s.get(DataComponents.STORED_ENCHANTMENTS);
    if (stored == null || stored.isEmpty())
      return null;

    Holder<Enchantment> h = null;
    int lvl = 0;
    for (Object2IntMap.Entry<Holder<Enchantment>> e : stored.entrySet()) {
      h = e.getKey();
      lvl = e.getIntValue();
      break;
    }
    if (h == null)
      return null;

    ResourceKey<Enchantment> key = h.unwrapKey().orElse(null);
    if (key == null)
      return null;

    int max = h.value().getMaxLevel();
    if (max <= 0)
      return null;

    lvl = clamp(lvl, 1, max);
    return new EnchData(key, lvl, max);
  }

  @Unique
  private static int clamp(int v, int lo, int hi) {
    return v < lo ? lo : (v > hi ? hi : v);
  }

  @Unique
  private static ResourceKey<Enchantment> rk(String path) {
    return ResourceKey.create(Registries.ENCHANTMENT, Identifier.withDefaultNamespace(path));
  }

  @Unique
  private static ResourceKey<Enchantment> rk(String namespace, String path) {
    return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(namespace, path));
  }

  @Unique
  private static void put(Map<EnchantPair, ResourceKey<Enchantment>> m,
      ResourceKey<Enchantment> a,
      ResourceKey<Enchantment> b,
      ResourceKey<Enchantment> out) {
    m.put(new EnchantPair(a, b), out);
  }

  @Unique
  private static Map<EnchantPair, ResourceKey<Enchantment>> buildMixMap() {
    Map<EnchantPair, ResourceKey<Enchantment>> m = new HashMap<>();

    put(m, rk("protection"), rk("fire_protection"), rk("fire_protection"));
    put(m, rk("protection"), rk("blast_protection"), rk("blast_protection"));
    put(m, rk("protection"), rk("projectile_protection"), rk("projectile_protection"));
    put(m, rk("fire_protection"), rk("blast_protection"), rk("protection"));
    put(m, rk("fire_protection"), rk("projectile_protection"), rk("protection"));
    put(m, rk("blast_protection"), rk("projectile_protection"), rk("protection"));
    put(m, rk("feather_falling"), rk("protection"), rk("feather_falling"));

    put(m, rk("smite"), rk("bane_of_arthropods"), rk("sharpness"));
    put(m, rk("sharpness"), rk("fire_aspect"), rk("fire_aspect"));
    put(m, rk("sharpness"), rk("knockback"), rk("knockback"));
    put(m, rk("sharpness"), rk("sweeping_edge"), rk("sweeping_edge"));

    put(m, rk("efficiency"), rk("fortune"), rk("fortune"));
    put(m, rk("silk_touch"), rk("efficiency"), rk("silk_touch"));
    put(m, rk("silk_touch"), rk("fortune"), rk("fortune"));
    put(m, rk("unbreaking"), rk("fortune"), rk("unbreaking"));

    put(m, rk("infinity"), rk("unbreaking"), rk("infinity"));
    put(m, rk("power"), rk("flame"), rk("flame"));
    put(m, rk("power"), rk("punch"), rk("power"));

    put(m, rk("piercing"), rk("multishot"), rk("quick_charge"));
    put(m, rk("piercing"), rk("quick_charge"), rk("piercing"));
    put(m, rk("multishot"), rk("quick_charge"), rk("multishot"));

    put(m, rk("loyalty"), rk("channeling"), rk("channeling"));
    put(m, rk("loyalty"), rk("impaling"), rk("impaling"));
    put(m, rk("riptide"), rk("loyalty"), rk("loyalty"));
    put(m, rk("riptide"), rk("impaling"), rk("impaling"));

    put(m, rk("density"), rk("breach"), rk("breach"));
    put(m, rk("wind_burst"), rk("density"), rk("wind_burst"));
    put(m, rk("wind_burst"), rk("breach"), rk("wind_burst"));

    put(m, rk("lunge"), rk("riptide"), rk("lunge"));
    put(m, rk("lunge"), rk("loyalty"), rk("lunge"));
    put(m, rk("lunge"), rk("impaling"), rk("impaling"));

    put(m, rk("binding_curse"), rk("vanishing_curse"), rk("vanishing_curse"));
    put(m, rk("binding_curse"), rk("protection"), rk("binding_curse"));
    put(m, rk("vanishing_curse"), rk("protection"), rk("vanishing_curse"));

    put(m, rk("fire_aspect"), rk("protection"), rk("fire_protection"));
    put(m, rk("thorns"), rk("projectile_protection"), rk("projectile_protection"));
    put(m, rk("aqua_affinity"), rk("blast_protection"), rk("blast_protection"));

    put(m, rk("frost_walker"), rk("feather_falling"), rk("depth_strider"));
    put(m, rk("depth_strider"), rk("respiration"), rk("aqua_affinity"));
    put(m, rk("bane_of_arthropods"), rk("knockback"), rk("sweeping_edge"));
    put(m, rk("smite"), rk("fire_aspect"), rk("fire_aspect"));
    put(m, rk("power"), rk("punch"), rk("flame"));
    put(m, rk("punch"), rk("flame"), rk("punch"));

    return m;
  }

  @Unique
  private record EnchantPair(ResourceKey<Enchantment> a, ResourceKey<Enchantment> b) {
    EnchantPair {
      if (a.toString().compareTo(b.toString()) > 0) {
        ResourceKey<Enchantment> t = a;
        a = b;
        b = t;
      }
    }
  }
}
