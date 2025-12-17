package com.justindugan.juganmod.spawns;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.BlockPos;

public class CustomSpawns {
    
    public static List<BlockPos> SPAWNS = new ArrayList<>();
    public static ServerLevel overworld;

    public static void init(){
        SPAWNS.add(new BlockPos(100, 100, 100));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> { //bc server can't be accessed until after server started
            overworld = server.overworld();
            BlockPos worldSpawn = overworld.getRespawnData().pos();
            System.out.println("World spawn: " + worldSpawn);
            SPAWNS.clear();
            SPAWNS = generateSpawns(worldSpawn, 1000, 4); //radius, numSpawns
            System.out.println("New spawns: " + SPAWNS);
        });
    }

    public static List<BlockPos> generateSpawns(BlockPos worldSpawn, int radius, int numSpawns) {
        List<BlockPos> spawnPoints = new ArrayList<>();
        for (int i = 0; i < numSpawns; i++) {
            double angle = 2 * Math.PI * i / numSpawns;
            int x = worldSpawn.getX() + (int)(radius * Math.cos(angle));
            int z = worldSpawn.getZ() + (int)(radius * Math.sin(angle));
            BlockPos newSpawn = new BlockPos(x, worldSpawn.getY(), z);
            newSpawn = findSafeY(overworld, newSpawn);
            spawnPoints.add(newSpawn);
        }
        return spawnPoints;
    }

    public static BlockPos findSafeY(ServerLevel level, BlockPos pos) {
        level.getChunkAt(pos);
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
    }

}
