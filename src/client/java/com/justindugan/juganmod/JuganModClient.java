package com.justindugan.juganmod;

import com.justindugan.juganmod.enchants.EnchantToolTip;
import com.justindugan.juganmod.mixin.EnchantMixins;

import net.fabricmc.api.ClientModInitializer;
public class JuganModClient implements ClientModInitializer {
	public static boolean locatorOffline = false;

	@Override
	public void onInitializeClient() {
		LocatorUI.init();
		EnchantToolTip.init();
	}
}