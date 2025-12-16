package com.justindugan.juganmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justindugan.juganmod.spawns.CustomSpawns;
import com.justindugan.juganmod.spawns.PlayerSpawnHandler;
import com.justindugan.juganmod.waypoints.PlayerMarkerHooks;
import com.justindugan.juganmod.waypoints.WaypointManager;
import com.justindugan.juganmod.waypoints.WaypointRanges;

public class JuganMod implements ModInitializer {
	public static final String MOD_ID = "juganmod";
	public static final Identifier LOCATOR_OFFLINE = Identifier.fromNamespaceAndPath(MOD_ID, "locator_offline");
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("JuganMod started.");
		FabricDefaultAttributeRegistry.register(
				EntityType.PLAYER,
				Player.createAttributes().add(JuganModAttributes.LOCATOR_ONLINE, 1.0));
		RulesEnforcer.init();
		PlayerMarkerHooks.init();
		JuganCommands.init();
		WaypointRanges.init();
		CustomSpawns.init();
		PlayerSpawnHandler.init();
		WaypointManager.init();
	}
}