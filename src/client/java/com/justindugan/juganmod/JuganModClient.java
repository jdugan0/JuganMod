package com.justindugan.juganmod;

import com.justindugan.juganmod.mixin.AnvilMenuMixin;

import net.fabricmc.api.ClientModInitializer;
public class JuganModClient implements ClientModInitializer {
	public static boolean locatorOffline = false;

	@Override
	public void onInitializeClient() {
		LocatorUI.init();
		EnchantToolTip.init();
	}
}