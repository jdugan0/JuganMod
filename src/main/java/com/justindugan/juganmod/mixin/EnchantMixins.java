package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.enchants.EnchantCapRules;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class EnchantMixins {
  @Shadow
  @Final
  private DataSlot cost;

  private static final int MAX_COST = 40;
  private static final int REPAIR_COST_CAP = 5;

  @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
  private int jugan$disableTooExpensive(int original) {
    return Integer.MAX_VALUE;
  }

  @Inject(method = "createResult", at = @At("TAIL"))
  private void jugan$afterCreateResult(CallbackInfo ci) {
    AnvilMenu self = (AnvilMenu) (Object) this;
    ItemStack result = self.getSlot(self.getResultSlot()).getItem();
    if (cost.get() > MAX_COST)
      cost.set(MAX_COST);
    if (!result.isEmpty()) {
      int cap = EnchantCapRules.getCap(result);
      int n = EnchantCapRules.countItemEnchants(result, false);
      if (cap > 0 && n > cap) {
        result = ItemStack.EMPTY;
        cost.set(0);
      }
    }
    result.set(
        DataComponents.REPAIR_COST,
        Math.min(result.getOrDefault(DataComponents.REPAIR_COST, 0), REPAIR_COST_CAP));
  }
}
