package com.justindugan.juganmod.mixin;

import com.justindugan.juganmod.ModDataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItem.class)
public class CompassLodestoneBindMixin {

    @Inject(method = "useOn", at = @At("RETURN"))
    private void juganmod_onBindToLodestone(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = ctx.getLevel();
        if (level.isClientSide()) return;
        if (!level.getBlockState(ctx.getClickedPos()).is(Blocks.LODESTONE)) return;
        if (!cir.getReturnValue().consumesAction()) return;

        ItemStack stack = ctx.getItemInHand();
        stack.remove(ModDataComponents.COMPASS_MODE);
    }
}
