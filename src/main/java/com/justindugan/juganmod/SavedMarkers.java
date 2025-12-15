package com.justindugan.juganmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

    public final class SavedMarkers extends SavedData {
        public record Marker(ResourceKey<Level> dimension, BlockPos pos, long gameTime) {
            public static final Codec<Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ResourceKey.codec(Registries.DIMENSION)
                            .fieldOf("dim").forGetter(Marker::dimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(Marker::pos),
                    Codec.LONG.fieldOf("t").forGetter(Marker::gameTime)).apply(instance, Marker::new));
        }

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    private static final Codec<Map<UUID, Marker>> LAST_DEATH_MAP_CODEC = Codec.unboundedMap(UUID_CODEC, Marker.CODEC);

    public static final Codec<SavedMarkers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LAST_DEATH_MAP_CODEC.optionalFieldOf("last_death", Map.of())
                    .forGetter(SavedMarkers::lastDeathView))
            .apply(instance, SavedMarkers::new));

    private static final SavedDataType<SavedMarkers> TYPE = new SavedDataType<>(
            "jugan_player_markers",
            SavedMarkers::new,
            CODEC,
            null);

    private final Map<UUID, Marker> lastDeath;

    public SavedMarkers() {
        this.lastDeath = new HashMap<>();
    }

    private SavedMarkers(Map<UUID, Marker> lastDeath) {
        this.lastDeath = new HashMap<>(lastDeath);
    }

    private Map<UUID, Marker> lastDeathView() {
        return this.lastDeath;
    }

    public static SavedMarkers get(MinecraftServer server) {
        ServerLevel level = server.overworld();
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void setLastDeath(UUID playerId, Marker marker) {
        lastDeath.put(playerId, marker);
        setDirty();
    }

    public Optional<Marker> getLastDeath(UUID playerId) {
        return Optional.ofNullable(lastDeath.get(playerId));
    }
    public Map<UUID, Marker> getMap(){
        return lastDeath;
    }
}