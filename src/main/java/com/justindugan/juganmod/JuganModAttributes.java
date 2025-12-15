package com.justindugan.juganmod;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class JuganModAttributes {
    private JuganModAttributes() {
    }

    public static final ResourceKey<Attribute> LOCATOR_ONLINE_KEY = ResourceKey.create(Registries.ATTRIBUTE,
            Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, "locator_online"));

    public static final Holder<Attribute> LOCATOR_ONLINE = register(
            LOCATOR_ONLINE_KEY,
            new RangedAttribute(
                    "attribute.juganmod.locator_online",
                    1.0, 0.0, 1.0).setSyncable(true));

    private static Holder<Attribute> register(ResourceKey<Attribute> key, Attribute attr) {
        Registry.register(BuiltInRegistries.ATTRIBUTE, key, attr);
        return BuiltInRegistries.ATTRIBUTE.getOrThrow(key);
    }
}
