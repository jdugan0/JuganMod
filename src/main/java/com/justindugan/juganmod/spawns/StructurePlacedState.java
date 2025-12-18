package com.justindugan.juganmod.spawns;

import com.mojang.serialization.Codec;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class StructurePlacedState extends SavedData {

    public boolean placed;

    // Codec for serialization
    public static final Codec<StructurePlacedState> CODEC = Codec.BOOL.xmap(
        placed -> new StructurePlacedState(placed),
        state -> state.placed
    );

    // Directly create a SavedDataType — NO create() method exists
    public static final SavedDataType<StructurePlacedState> TYPE =
        new SavedDataType<>("juganmod_structures", StructurePlacedState::new, CODEC, DataFixTypes.LEVEL);

    public StructurePlacedState() {
        this(false);
    }

    public StructurePlacedState(boolean placed) {
        this.placed = placed;
    }

    // Load or create the persistent state
    public static StructurePlacedState get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(TYPE);
    }
}
