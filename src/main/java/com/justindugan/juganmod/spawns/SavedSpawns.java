package com.justindugan.juganmod.spawns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class SavedSpawns extends SavedData {
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    private static final Codec<Map<UUID, BlockPos>> SPAWN_MAP_CODEC = Codec.unboundedMap(UUID_CODEC, BlockPos.CODEC);

    public static final Codec<SavedSpawns> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SPAWN_MAP_CODEC.optionalFieldOf("player_spawn", Map.of())
                    .forGetter(SavedSpawns::playerSpawnsView))
            .apply(instance, SavedSpawns::new));

    private final Map<UUID, BlockPos> playerSpawns;

    private static final SavedDataType<SavedSpawns> TYPE = new SavedDataType<>(
            "jugan_player_spawns",
            SavedSpawns::new,
            CODEC,
            null);

    public SavedSpawns(Map<UUID, BlockPos> playerSpawns) {
        this.playerSpawns = new HashMap<>(playerSpawns);
    }

    public SavedSpawns() {
        playerSpawns = new HashMap<>();
    }

    private Map<UUID, BlockPos> playerSpawnsView() {
        return Map.copyOf(playerSpawns);
    }

    public static SavedSpawns get(MinecraftServer server) {
        ServerLevel level = server.overworld();
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void setSpawn(UUID playerId, BlockPos pos) {
        playerSpawns.put(playerId, pos);
        setDirty();
    }

    public Optional<BlockPos> getSpawn(UUID playerId) {
        return Optional.ofNullable(playerSpawns.get(playerId));
    }

    public boolean contains(UUID playerId) {
        return playerSpawns.containsKey(playerId);
    }

    public int size() {
        return playerSpawns.size();
    }

}
