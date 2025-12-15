package com.justindugan.juganmod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSpawnHandler {
    
    private static final Map<UUID, BlockPos> spawns = new HashMap<>();

    public static void init() {
    /*
    ServerPlayerEvents.AFTER_RESPAWN.register
    on join -> check if player exists in hashmap. if no, assign them a spawn position spawnList[amount of players in total joined mod 4]. 
        spawn position is assigned by adding key value pair of UUID+spawn position into map. then, teleport them to the assigned position. 
    on respawn -> check if respawn config exists. if no, teleport them to assigned spawn position by grabbing it from map.
    */

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            //if (oldPlayer.RespawnConfig == null) {
            //    return; // Let the default respawn behavior handle it
            //}
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (!spawns.containsKey(player.getUUID())){
                BlockPos spawnPosition = CustomSpawns.SPAWNS.get(spawns.size() % CustomSpawns.SPAWNS.size()); //spawns.size()->#players, SPAWNS.size()->#spawn points
                spawns.put(player.getUUID(), spawnPosition);
                
                player.connection.teleport( //teleport them
                    spawnPosition.getX() + 0.5,
                    spawnPosition.getY(),
                    spawnPosition.getZ() + 0.5,
                    player.getYRot(),
                    player.getXRot()
                );
                System.out.println("Assigning spawn for player " + player.getName().getString() + " at " + spawnPosition);
                System.out.println("Teleporting player " + player.getName().getString() + " to " + spawnPosition);
            }
        });
    }

}