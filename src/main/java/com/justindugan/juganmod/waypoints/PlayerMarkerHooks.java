package com.justindugan.juganmod.waypoints;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public final class PlayerMarkerHooks {
    private PlayerMarkerHooks() {}

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register(PlayerMarkerHooks::afterDeath);
    }

    private static void afterDeath(LivingEntity entity, DamageSource source) {
        if (entity instanceof ServerPlayer player) {
            PlayerMarkers.setLastDeath(player);
        }
    }
}
