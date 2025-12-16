package com.justindugan.juganmod.spawns;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;

public class PlayerSpawnHandler {
    public static void init() {
        /*
         * ServerPlayerEvents.AFTER_RESPAWN.register
         * on join -> check if player exists in hashmap. if no, assign them a spawn
         * position spawnList[amount of players in total joined mod 4].
         * spawn position is assigned by adding key value pair of UUID+spawn position
         * into map. then, teleport them to the assigned position.
         * on respawn -> check if respawn config exists. if no, teleport them to
         * assigned spawn position by grabbing it from map.
         */

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.getRespawnConfig() == null) {
                SavedSpawns save = SavedSpawns.get(newPlayer.level().getServer());
                BlockPos spawnPosition = save.getSpawn(newPlayer.getUUID()).orElse(null);
                if (spawnPosition == null) {
                    return;
                }
                ServerLevel level = newPlayer.level().getServer().overworld();
                level.getChunkAt(spawnPosition);
                newPlayer.teleportTo(level,
                        spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5,
                        EnumSet.noneOf(Relative.class),
                        newPlayer.getYRot(),
                        newPlayer.getXRot(),
                        true);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            SavedSpawns save = SavedSpawns.get(server);
            if (!save.contains(player.getUUID())) {
                BlockPos spawnPosition = CustomSpawns.SPAWNS.get(save.size() % CustomSpawns.SPAWNS.size()); // spawns.size()->#players,
                                                                                                            // SPAWNS.size()->#spawn
                                                                                                            // points
                save.setSpawn(player.getUUID(), spawnPosition);
                ServerLevel level = server.overworld();

                level.getChunkAt(spawnPosition);
                player.teleportTo(
                        level,
                        spawnPosition.getX() + 0.5, spawnPosition.getY(), spawnPosition.getZ() + 0.5,
                        EnumSet.noneOf(Relative.class),
                        player.getYRot(),
                        player.getXRot(),
                        true);
            }
        });
    }

}