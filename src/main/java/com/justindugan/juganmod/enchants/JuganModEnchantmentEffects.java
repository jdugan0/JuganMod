package com.justindugan.juganmod.enchants;

import com.justindugan.juganmod.JuganMod;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

public class JuganModEnchantmentEffects {
    public static final ResourceKey<Enchantment> AREA = key("area");
    public static final ResourceKey<Enchantment> SCHOLAR = key("scholar");
    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, path));
    }

    private JuganModEnchantmentEffects() {}
}
