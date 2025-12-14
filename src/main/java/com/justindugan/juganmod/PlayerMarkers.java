package com.justindugan.juganmod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerMarkers {
    private PlayerMarkers() {
    }

    public record Marker(ResourceKey<Level> dimension, BlockPos pos, long gameTime) {
    }

    private static final Map<UUID, Marker> LAST_DEATH = new ConcurrentHashMap<>();

    public static void setLastDeath(ServerPlayer player) {
        ServerLevel level = player.level();
        JuganMod.LOGGER.info("Player died at: " + player.blockPosition());
        LAST_DEATH.put(
                player.getUUID(),
                new Marker(level.dimension(), player.blockPosition(), level.getGameTime()));
    }

    public static Marker getLastDeath(ServerPlayer player) {
        return LAST_DEATH.get(player.getUUID());
    }

    public static Optional<Marker> getRespawnMarker(ServerPlayer player) {
        RespawnConfig config = player.getRespawnConfig();
        if (config == null)
            return Optional.empty();

        ResourceKey<Level> dim = config.respawnData().dimension();
        BlockPos pos = config.respawnData().pos();
        long t = player.level().getGameTime();
        return Optional.of(new Marker(dim, pos, t));
    }
}
