package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.enchants.EnchantCapRules;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public abstract class EnchantMixins {
  @Inject(method = "createResult", at = @At("TAIL"))
  private void jugan$enforceEnchantCap(CallbackInfo ci) {
    AnvilMenu self = (AnvilMenu) (Object) this;

    ItemStack result = self.getSlot(self.getResultSlot()).getItem();
    if (result.isEmpty())
      return;

    int cap = EnchantCapRules.getCap(result);
    int n = EnchantCapRules.countItemEnchants(result, false);
    if (n > cap) {
      self.getSlot(self.getResultSlot()).set(ItemStack.EMPTY);
    }
  }
}
