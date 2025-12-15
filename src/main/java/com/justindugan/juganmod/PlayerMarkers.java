package com.justindugan.juganmod;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.justindugan.juganmod.SavedMarkers.Marker;

public final class PlayerMarkers {
    private PlayerMarkers() {
    }

    public static void setLastDeath(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        ServerLevel level = player.level();
        SavedMarkers data = SavedMarkers.get(server);

        data.setLastDeath(
                player.getUUID(),
                new SavedMarkers.Marker(level.dimension(), player.blockPosition(), level.getGameTime()));
    }

    public static Optional<SavedMarkers.Marker> getLastDeath(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        return SavedMarkers.get(server).getLastDeath(player.getUUID());
    }

    public static Map<UUID, Marker> getDeathMap(MinecraftServer server) {
        return SavedMarkers.get(server).getMap();
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
