package com.justindugan.juganmod.enchants;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantmentGenerator extends FabricDynamicRegistryProvider {
    public ModEnchantmentGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        var supported = registries.lookupOrThrow(Registries.ITEM).getOrThrow(ItemTags.MINING_ENCHANTABLE);

        entries.add(JuganModEnchantmentEffects.AREA,
            Enchantment.enchantment(
                Enchantment.definition(
                    supported,
                    /* weight */ 1,
                    /* max_level */ 1,
                    Enchantment.dynamicCost(25, 0),
                    Enchantment.dynamicCost(45, 0),
                    /* anvil_cost */ 8,
                    EquipmentSlotGroup.HAND
                )
            )
            .build(JuganModEnchantmentEffects.AREA.identifier())
        );
    }

    @Override
    public String getName() {
        return "JuganMod Enchantments";
    }
}
