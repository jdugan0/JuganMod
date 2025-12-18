package com.justindugan.juganmod.spawns;

import java.io.InputStream;

import com.justindugan.juganmod.JuganMod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class SpawnStructures {

    public static void init() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            world.getServer().execute(() -> tryPlaceStructures((ServerLevel) world));
        });
    }

    public static void tryPlaceStructures(ServerLevel world) {
        System.out.println("Checking if spawns are ready...");
        if (CustomSpawns.SPAWNS.size() < 4) {
            world.getServer().execute(() -> tryPlaceStructures((ServerLevel) world));
            return;
        }
        System.out.println("Spawns are ready! Attempting placements...");
        StructurePlacedState state = StructurePlacedState.get(world);
        if (state.placed) return;

        if (world.dimension() != Level.OVERWORLD) return;

        placeStructure(world, Identifier.fromNamespaceAndPath("juganmod", "spawn_pink"), CustomSpawns.SPAWNS.get(0));
        placeStructure(world, Identifier.fromNamespaceAndPath("juganmod", "spawn_green"), CustomSpawns.SPAWNS.get(1));
        placeStructure(world, Identifier.fromNamespaceAndPath("juganmod", "spawn_orange"), CustomSpawns.SPAWNS.get(2));
        placeStructure(world, Identifier.fromNamespaceAndPath("juganmod", "spawn_blue"), CustomSpawns.SPAWNS.get(3));

        BlockPos center = new BlockPos(0, 0, 0);
        center = CustomSpawns.findSafeY((ServerLevel)world, center);
        placeStructure(world, Identifier.fromNamespaceAndPath("juganmod", "stone_hub"), center);
        state.placed = true;
        state.setDirty();
    }

    public static void placeStructure(ServerLevel world, Identifier id, BlockPos pos) {
        StructureTemplate template = loadTemplate(world, id);
        System.out.println("Attempting to place structure...");

        if (template == null) return;

        StructurePlaceSettings settings = new StructurePlaceSettings();

        template.placeInWorld(world, pos.offset(-4, -2, -4), pos, settings,world.random,Block.UPDATE_ALL);
        System.out.println("Placed spawn structures at custom spawns.");
        //
    }

    public static StructureTemplate loadTemplate(ServerLevel world, Identifier id) {
    String path = "/data/" + id.getNamespace() + "/structure/" + id.getPath() + ".nbt";

    try (InputStream stream = JuganMod.class.getResourceAsStream(path)) {
        if (stream == null) {
            System.out.println("Failed to find structure NBT: " + path);
            return null;
        }
        //CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap()); // if compressed, otherwise use read()
        StructureTemplate template = new StructureTemplate();

        template = world.getServer().getStructureManager().get(id).orElse(null);
        //ResourceKey<Registry<Block>> blockKey = net.minecraft.core.registries.Registries.BLOCK;
        //HolderGetter<Block> holderGetter = world.registryAccess().holderGetter(blockKey);

        //template.load(holderGetter, nbt);
        
        return template;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


}
