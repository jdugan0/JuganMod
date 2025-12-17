package com.justindugan.juganmod;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModComponents {
    public static final DataComponentType<Integer> EXTRACT_SEED = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("juganmod", "extract_seed"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    public static void init() {
    }
}