package com.justindugan.juganmod;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JuganMod implements ModInitializer {
	public static final String MOD_ID = "juganmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("JuganMod started.");
		RulesEnforcer.init();
		LocatorBarNightToggle.init();
	}
}