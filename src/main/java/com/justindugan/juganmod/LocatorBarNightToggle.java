package com.justindugan.juganmod;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

public final class LocatorBarNightToggle {
    private LocatorBarNightToggle() {}
    private static final Object2BooleanOpenHashMap<ServerLevel> lastEnabled = new Object2BooleanOpenHashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(LocatorBarNightToggle::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if ((server.getTickCount() % 20) != 0) return;

        for (ServerLevel level : server.getAllLevels()) {
            boolean enable = !DayNight.isNight(level);

            boolean prev = lastEnabled.getOrDefault(level, !enable);
            if (prev == enable) continue;

            level.getGameRules().set(GameRules.LOCATOR_BAR, enable, server);
            lastEnabled.put(level, enable);
        }
    }
}
