package com.justindugan.juganmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$2")
public abstract class GrindstoneInputSlot1Mixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void jugan$allowLodestoneCompass(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.COMPASS) && stack.has(DataComponents.LODESTONE_TRACKER)) {
            cir.setReturnValue(true);
        }
    }
}
