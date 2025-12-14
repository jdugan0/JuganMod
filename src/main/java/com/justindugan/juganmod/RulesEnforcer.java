package com.justindugan.juganmod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRules;

public class RulesEnforcer {
    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(RulesEnforcer::applyAllWorlds);
        ServerWorldEvents.LOAD.register((server, world) -> applyWorldRules(world.getGameRules(), server));
    }

    private static void applyAllWorlds(MinecraftServer server) {
        server.getAllLevels().forEach(world -> applyWorldRules(world.getGameRules(), server));
    }

    private static void applyWorldRules(GameRules rules, MinecraftServer server) {
        rules.set(GameRules.REDUCED_DEBUG_INFO, true, server);
        rules.set(GameRules.PLAYERS_SLEEPING_PERCENTAGE, 101, server);
    }
}
