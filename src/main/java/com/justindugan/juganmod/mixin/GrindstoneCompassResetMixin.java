package com.justindugan.juganmod.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneCompassResetMixin {

    @Shadow
    @Final
    private Container inputSlots;
    @Shadow
    @Final
    private ResultContainer resultSlots;

    @Inject(method = "createResult", at = @At("TAIL"))
    private void juganmod_resetLodestoneCompass(CallbackInfo ci) {
        ItemStack a = inputSlots.getItem(0);
        ItemStack b = inputSlots.getItem(1);

        if (!b.isEmpty())
            return;
        if (!(a.getItem() instanceof CompassItem))
            return;
        if (!a.has(DataComponents.LODESTONE_TRACKER))
            return;

        resultSlots.setItem(0, new ItemStack(Items.COMPASS));
    }
}
