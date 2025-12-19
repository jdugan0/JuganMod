package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {
  @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
  private void jugan$lodestoneCompassToCompass(ItemStack a, ItemStack b, CallbackInfoReturnable<ItemStack> cir) {
    ItemStack in = (!a.isEmpty() && b.isEmpty()) ? a : (a.isEmpty() && !b.isEmpty()) ? b : ItemStack.EMPTY;

    if (!in.isEmpty() && in.is(Items.COMPASS) && in.has(DataComponents.LODESTONE_TRACKER)) {
      cir.setReturnValue(new ItemStack(Items.COMPASS));
    }
  }
}
