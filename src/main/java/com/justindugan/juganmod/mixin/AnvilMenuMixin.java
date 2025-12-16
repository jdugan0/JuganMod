package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.EnchantCapRules;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
  @Shadow
  @Final
  private DataSlot cost;

  private static final int MAX_COST = 35;
  private static final int REPAIR_COST_CAP = 2;

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

}
