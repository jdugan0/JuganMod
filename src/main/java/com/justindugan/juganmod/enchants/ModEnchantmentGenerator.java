package com.justindugan.juganmod.enchants;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantmentGenerator extends FabricDynamicRegistryProvider {
    public ModEnchantmentGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final TagKey<Item> SCHOLAR_ENCHANTABLE = TagKey.create(Registries.ITEM,
            Identifier.parse("juganmod:scholar_enchantable"));

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {

        entries.add(JuganModEnchantmentEffects.AREA,
                Enchantment.enchantment(
                        Enchantment.definition(
                                registries.lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                /* weight */ 1,
                                /* max_level */ 1,
                                Enchantment.dynamicCost(25, 0),
                                Enchantment.dynamicCost(45, 0),
                                /* anvil_cost */ 8,
                                EquipmentSlotGroup.HAND))
                        .build(JuganModEnchantmentEffects.AREA.identifier()));

        entries.add(JuganModEnchantmentEffects.SCHOLAR,
                Enchantment.enchantment(
                        Enchantment.definition(
                                registries.lookupOrThrow(Registries.ITEM).getOrThrow(SCHOLAR_ENCHANTABLE),
                                /* weight */ 8,
                                /* max_level */ 3,
                                Enchantment.dynamicCost(15, 9),
                                Enchantment.dynamicCost(65, 9),
                                /* anvil_cost */ 8,
                                EquipmentSlotGroup.HAND))
                        .build(JuganModEnchantmentEffects.SCHOLAR.identifier()));
    }

    @Override
    public String getName() {
        return "JuganMod Enchantments";
    }
}
