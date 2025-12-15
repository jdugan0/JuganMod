package com.justindugan.juganmod;

import net.fabricmc.api.ClientModInitializer;
public class JuganModClient implements ClientModInitializer {
	public static boolean locatorOffline = false;

	@Override
	public void onInitializeClient() {
		LocatorUI.init();
	}
}