package com.justindugan.juganmod.loot;


import com.justindugan.juganmod.enchants.JuganModEnchantmentEffects;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class AreaTreasureLoot {
    private AreaTreasureLoot() {}

    private static final float CHANCE = 0.01f;
    private static final int ROLLS = 1;

    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) return;

            if (!TARGET_TABLES.contains(key)) return;

            tableBuilder.withPool(buildAreaBookPool(registries, CHANCE, ROLLS));
        });
    }

    private static LootPool.Builder buildAreaBookPool(
            HolderLookup.Provider registries,
            float chance,
            int rolls
    ) {
        var enchLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> area = enchLookup.getOrThrow(JuganModEnchantmentEffects.AREA);

        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(rolls))
                .when(LootItemRandomChanceCondition.randomChance(chance))
                .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .apply(new SetEnchantmentsFunction.Builder(false)
                                .withEnchantment(area, ConstantValue.exactly(1))));
    }

    private static final java.util.Set<ResourceKey<LootTable>> TARGET_TABLES = java.util.Set.of(
            BuiltInLootTables.WOODLAND_MANSION,
            BuiltInLootTables.END_CITY_TREASURE,
            BuiltInLootTables.BASTION_TREASURE,
            BuiltInLootTables.ANCIENT_CITY
    );
}
