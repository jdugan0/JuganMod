package com.justindugan.juganmod;

import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.Codec;


public class ModDataComponents {

    public static final DataComponentType<Integer> COMPASS_MODE =
            DataComponentType.<Integer>builder().persistent(Codec.INT).build();

    public static void register() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, "juganmod:compass_mode",COMPASS_MODE);
    }
}
