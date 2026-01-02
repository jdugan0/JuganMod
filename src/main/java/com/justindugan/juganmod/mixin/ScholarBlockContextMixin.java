
package com.justindugan.juganmod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.xp.XpBonusContext;

@Mixin(ServerPlayerGameMode.class)
public abstract class ScholarBlockContextMixin {
    @Shadow
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At("HEAD"))
    private void jugan$push(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ItemStack tool = player.getMainHandItem();
        XpBonusContext.push(player, tool);
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void jugan$pop(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        XpBonusContext.pop();
    }
}
