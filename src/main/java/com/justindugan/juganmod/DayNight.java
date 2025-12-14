package com.justindugan.juganmod;

import net.minecraft.server.level.ServerLevel;

public final class DayNight {
    private DayNight() {}
    public static boolean isNight(ServerLevel level) {
        long t = level.getDayTime() % 24000L;
        return t >= 12000L;
    }
}
