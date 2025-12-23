package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.ModComponents;
import com.justindugan.juganmod.enchants.EnchantCapRules;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
  @Shadow
  @Final
  private DataSlot cost;
  @Shadow
  private int repairItemCountCost;
  private static final int MAX_COST = 65;
  private static final int REPAIR_COST_CAP = 10000;

  @Redirect(method = "createResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z", ordinal = 1), require = 1)
  private boolean jugan$disableTooExpensiveCheck(Player player) {
    return true;
  }

  @Inject(method = "createResult", at = @At("TAIL"))
  private void jugan$afterCreateResult(CallbackInfo ci) {
    AnvilMenu self = (AnvilMenu) (Object) this;
    ItemStack result = self.getSlot(self.getResultSlot()).getItem();

    if (cost.get() > MAX_COST)
      cost.set(MAX_COST);

    if (result.isEmpty())
      return;

    int cap = EnchantCapRules.getCap(result);
    int n = EnchantCapRules.countItemEnchants(result, false);
    if (cap > 0 && n > cap) {
      cost.set(0);
      self.getSlot(self.getResultSlot()).set(ItemStack.EMPTY);
      return;
    }

    result.set(DataComponents.REPAIR_COST,
        Math.min(result.getOrDefault(DataComponents.REPAIR_COST, 0), REPAIR_COST_CAP));
  }

  @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
  private void jugan$extractRandomEnchantToBook(CallbackInfo ci) {
    AnvilMenu self = (AnvilMenu) (Object) this;

    ItemStack left = self.getSlot(0).getItem();
    ItemStack right = self.getSlot(1).getItem();

    if (left.isEmpty() || right.isEmpty())
      return;
    if (!right.is(net.minecraft.world.item.Items.BOOK))
      return;

    boolean leftIsBook = left.is(net.minecraft.world.item.Items.ENCHANTED_BOOK);

    var enchComp = leftIsBook
        ? left.get(DataComponents.STORED_ENCHANTMENTS)
        : left.get(DataComponents.ENCHANTMENTS);

    if (enchComp == null || enchComp.isEmpty())
      return;

    var entries = new java.util.ArrayList<>(enchComp.entrySet());
    if (entries.isEmpty())
      return;

    Integer seed = left.get(ModComponents.EXTRACT_SEED);
    if (seed == null) {
      RandomSource rnd = RandomSource.create();
      int newSeed = rnd.nextInt() & 0x7fffffff;
      left.set(ModComponents.EXTRACT_SEED, newSeed);
      self.getSlot(0).set(left);
      seed = newSeed;
    }

    int idx = Math.floorMod(seed, entries.size());
    var picked = entries.get(idx);

    var enchantHolder = picked.getKey();
    int level = picked.getIntValue();

    ItemStack out = new ItemStack(net.minecraft.world.item.Items.ENCHANTED_BOOK);

    var single = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(
        net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
    single.set(enchantHolder, level);

    out.set(DataComponents.STORED_ENCHANTMENTS, single.toImmutable());

    this.repairItemCountCost = 1;

    int xpCost = Math.min(MAX_COST, 8 + 2 * level);
    cost.set(xpCost);

    self.getSlot(self.getResultSlot()).set(out);

    out.set(DataComponents.REPAIR_COST,
        Math.min(out.getOrDefault(DataComponents.REPAIR_COST, 0), REPAIR_COST_CAP));

    ci.cancel();
  }

}
