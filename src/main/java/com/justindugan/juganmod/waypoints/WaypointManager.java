package com.justindugan.juganmod.waypoints;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import com.justindugan.juganmod.DayNight;
import com.justindugan.juganmod.JuganMod;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

public final class WaypointManager {

    static final ResourceKey<WaypointStyleAsset> DEATH_STYLE = ResourceKey.create(WaypointStyleAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, "death"));

    static final ResourceKey<WaypointStyleAsset> RESPAWN_STYLE = ResourceKey.create(WaypointStyleAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, "respawn"));

    static UUID deathWaypointId(UUID playerUuid) {
        return UUID.nameUUIDFromBytes((JuganMod.MOD_ID + ":death:" + playerUuid).getBytes(StandardCharsets.UTF_8));
    }

    static UUID respawnWaypointId(UUID playerUuid) {
        return UUID.nameUUIDFromBytes((JuganMod.MOD_ID + ":respawn:" + playerUuid).getBytes(StandardCharsets.UTF_8));
    }

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.player));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, dest) -> sync(player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> sync(newPlayer));
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (ServerPlayer p : world.players())
                sync(p);
        });
    }

    static Waypoint.Icon deathIcon() {
        Waypoint.Icon icon = new Waypoint.Icon();
        icon.style = DEATH_STYLE;
        icon.color = Optional.of(0xFFFFFF);
        return icon;
    }

    static Waypoint.Icon respawnIcon() {
        Waypoint.Icon icon = new Waypoint.Icon();
        icon.style = RESPAWN_STYLE;
        icon.color = Optional.of(0xFFFFFF);
        ;
        return icon;
    }

    static void removeDeath(ServerPlayer p) {
        UUID id = deathWaypointId(p.getUUID());
        p.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(id));
    }

    static void removeRespawn(ServerPlayer p) {
        UUID id = respawnWaypointId(p.getUUID());
        p.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(id));
    }

    static void sync(ServerPlayer p) {
        boolean night = DayNight.isNight(p.level());
        if (night) {
            removeDeath(p);
            removeRespawn(p);
            return;
        }

        PlayerMarkers.getLastDeath(p).ifPresentOrElse(marker -> {
            if (marker.dimension().equals(p.level().dimension())) {
                removeDeath(p);
                p.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(
                        deathWaypointId(p.getUUID()), deathIcon(), (marker.pos())));
            } else {
                removeDeath(p);
            }
        }, () -> removeDeath(p));

        PlayerMarkers.getRespawnMarker(p).ifPresentOrElse(marker -> {
            if (marker.dimension().equals(p.level().dimension())) {
                removeRespawn(p);
                p.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(
                        respawnWaypointId(p.getUUID()), respawnIcon(), (marker.pos())));
            } else {
                removeRespawn(p);
            }
        }, () -> removeRespawn(p));
    }

}
