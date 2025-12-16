package com.justindugan.juganmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class DisableVanillaEnchantedBookTradeMixin {
	@Inject(method = "getOffer", at = @At("HEAD"), cancellable = true)
	private void juganmod_disable(ServerLevel serverLevel, Entity entity, RandomSource randomSource,
			CallbackInfoReturnable<MerchantOffer> cir) {
		cir.setReturnValue(null);
	}
}
