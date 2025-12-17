package com.justindugan.juganmod.loot;

import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;

public final class StructureBookLoot {
        private StructureBookLoot() {
        }

        private static final float DEFAULT_COMMON_CHANCE = 0.1f;
        private static final float DEFAULT_RARE_CHANCE = 0.03f;
        private static final int DEFAULT_COMMON_ROLLS = 1;
        private static final int DEFAULT_RARE_ROLLS = 1;

        public static void init() {
                LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                        if (!source.isBuiltin())
                                return;

                        StructureBooks cfg = BOOKS_BY_TABLE.get(key);
                        if (cfg == null)
                                return;

                        if (!cfg.common().isEmpty() && cfg.commonChance() > 0f) {
                                tableBuilder.withPool(buildBookPool(
                                                registries, cfg.commonChance(), DEFAULT_COMMON_ROLLS, cfg.common()));
                        }

                        if (!cfg.rare().isEmpty() && cfg.rareChance() > 0f) {
                                tableBuilder.withPool(buildBookPool(
                                                registries, cfg.rareChance(), DEFAULT_RARE_ROLLS, cfg.rare()));
                        }
                });
        }

        private record StructureBooks(float commonChance, float rareChance,
                        List<BookSpec> common, List<BookSpec> rare) {
        }

        private record BookSpec(ResourceKey<Enchantment> enchant,
                        NumberProvider level,
                        int weight) {
        }

        private static LootPool.Builder buildBookPool(
                        HolderLookup.Provider registries,
                        float poolChance,
                        int rolls,
                        List<BookSpec> books) {
                var enchLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

                LootPool.Builder pool = LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(rolls))
                                .when(LootItemRandomChanceCondition.randomChance(poolChance));

                for (BookSpec spec : books) {
                        Holder<Enchantment> ench = enchLookup.getOrThrow(spec.enchant());

                        pool.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                                        .setWeight(spec.weight())
                                        .apply(new SetEnchantmentsFunction.Builder(false)
                                                        .withEnchantment(ench, spec.level())));
                }

                return pool;
        }

        private static NumberProvider lvl(int level) {
                return ConstantValue.exactly(level);
        }

        private static NumberProvider lvl(int min, int max) {
                return UniformGenerator.between(min, max);
        }

        // IMPORTANT: key this map by the SAME type you receive in the callback: `key`
        private static final Map<ResourceKey<LootTable>, StructureBooks> BOOKS_BY_TABLE = Map.ofEntries(
                        // igloo_chest: Frost Walker I–II; Protection III; Silk Touch I
                        Map.entry(BuiltInLootTables.IGLOO_CHEST, new StructureBooks(
                                        0.32f, 0.07f,
                                        List.of(
                                                        new BookSpec(Enchantments.FROST_WALKER, lvl(1, 2), 3),
                                                        new BookSpec(Enchantments.PROTECTION, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))),

                        // simple_dungeon: Protection III; Sharpness III; Power III; Unbreaking II;
                        // Looting I; Fortune I; Mending I (rare)
                        Map.entry(BuiltInLootTables.SIMPLE_DUNGEON, new StructureBooks(
                                        DEFAULT_COMMON_CHANCE, DEFAULT_RARE_CHANCE,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(3), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(3), 3),
                                                        new BookSpec(Enchantments.POWER, lvl(3), 3),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(2), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(1), 2),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(1), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // abandoned_mineshaft: Efficiency III; Unbreaking II; Fortune I; Feather
                        // Falling III; Silk Touch I (rare)
                        Map.entry(BuiltInLootTables.ABANDONED_MINESHAFT, new StructureBooks(
                                        DEFAULT_COMMON_CHANCE, DEFAULT_RARE_CHANCE,
                                        List.of(
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(3), 3),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(2), 3),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(1), 2),
                                                        new BookSpec(Enchantments.FEATHER_FALLING, lvl(3), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))),

                        // shipwreck_supply: Luck of the Sea II; Lure II
                        Map.entry(BuiltInLootTables.SHIPWRECK_SUPPLY, new StructureBooks(
                                        0.35f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.LUCK_OF_THE_SEA, lvl(2), 3),
                                                        new BookSpec(Enchantments.LURE, lvl(2), 3)),
                                        List.of())),

                        // shipwreck_treasure: Luck III; Lure III; Depth Strider III; Feather Falling
                        // IV; Silk Touch I
                        Map.entry(BuiltInLootTables.SHIPWRECK_TREASURE, new StructureBooks(
                                        0.40f, 0.08f,
                                        List.of(
                                                        new BookSpec(Enchantments.LUCK_OF_THE_SEA, lvl(3), 3),
                                                        new BookSpec(Enchantments.LURE, lvl(3), 3),
                                                        new BookSpec(Enchantments.DEPTH_STRIDER, lvl(3), 2),
                                                        new BookSpec(Enchantments.FEATHER_FALLING, lvl(4), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))),

                        // underwater_ruin_small: Respiration II; Aqua Affinity I; Impaling IV; Loyalty
                        // II
                        Map.entry(BuiltInLootTables.UNDERWATER_RUIN_SMALL, new StructureBooks(
                                        0.33f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.RESPIRATION, lvl(2), 3),
                                                        new BookSpec(Enchantments.AQUA_AFFINITY, lvl(1), 3),
                                                        new BookSpec(Enchantments.IMPALING, lvl(4), 2),
                                                        new BookSpec(Enchantments.LOYALTY, lvl(2), 2)),
                                        List.of())),

                        // underwater_ruin_big: Respiration III; Aqua Affinity I; Impaling V; Loyalty
                        // III; Riptide II; Mending I (rare)
                        Map.entry(BuiltInLootTables.UNDERWATER_RUIN_BIG, new StructureBooks(
                                        0.38f, 0.07f,
                                        List.of(
                                                        new BookSpec(Enchantments.RESPIRATION, lvl(3), 3),
                                                        new BookSpec(Enchantments.AQUA_AFFINITY, lvl(1), 3),
                                                        new BookSpec(Enchantments.IMPALING, lvl(5), 2),
                                                        new BookSpec(Enchantments.LOYALTY, lvl(3), 2),
                                                        new BookSpec(Enchantments.RIPTIDE, lvl(2), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // buried_treasure: Fortune II; Efficiency IV; Unbreaking III; Silk Touch I;
                        // Mending I
                        Map.entry(BuiltInLootTables.BURIED_TREASURE, new StructureBooks(
                                        0.42f, 0.10f,
                                        List.of(
                                                        new BookSpec(Enchantments.FORTUNE, lvl(2), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(4), 3),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1),
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // desert_pyramid: Protection IV; Sharpness IV; Looting II; Fortune II;
                        // Unbreaking III; Silk Touch I (rare)
                        Map.entry(BuiltInLootTables.DESERT_PYRAMID, new StructureBooks(
                                        0.34f, 0.06f,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(2), 2),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))),

                        // jungle_temple: Sharpness IV; Bane IV; Looting II; Efficiency IV; Fortune II;
                        // Mending I (rare)
                        Map.entry(BuiltInLootTables.JUNGLE_TEMPLE, new StructureBooks(
                                        0.34f, 0.06f,
                                        List.of(
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.BANE_OF_ARTHROPODS, lvl(4), 2),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(4), 3),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(2), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // pillager_outpost: Power IV; Punch II; Piercing IV; Quick Charge II
                        Map.entry(BuiltInLootTables.PILLAGER_OUTPOST, new StructureBooks(
                                        0.30f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.POWER, lvl(4), 3),
                                                        new BookSpec(Enchantments.PUNCH, lvl(2), 2),
                                                        new BookSpec(Enchantments.PIERCING, lvl(4), 2),
                                                        new BookSpec(Enchantments.QUICK_CHARGE, lvl(2), 2)),
                                        List.of())),

                        // ruined_portal: Fire Prot IV; Soul Speed I–II; Unbreaking III; Mending I
                        // (rare)
                        Map.entry(BuiltInLootTables.RUINED_PORTAL, new StructureBooks(
                                        0.34f, 0.06f,
                                        List.of(
                                                        new BookSpec(Enchantments.FIRE_PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SOUL_SPEED, lvl(1, 2), 2),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // nether_bridge: Sharpness IV; Smite V; Looting II; Fire Prot IV
                        Map.entry(BuiltInLootTables.NETHER_BRIDGE, new StructureBooks(
                                        0.34f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.SMITE, lvl(5), 2),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.FIRE_PROTECTION, lvl(4), 3)),
                                        List.of())),

                        // stronghold corridor/crossing: ... + Silk Touch (rare) / Mending (rare)
                        Map.entry(BuiltInLootTables.STRONGHOLD_CORRIDOR, new StructureBooks(
                                        0.36f, 0.07f,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.POWER, lvl(4), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(2), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))),
                        Map.entry(BuiltInLootTables.STRONGHOLD_CROSSING, new StructureBooks(
                                        0.36f, 0.07f,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.POWER, lvl(4), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(2), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // stronghold_library: top tier: Protection V; Sharpness V; Power V; Efficiency
                        // V; Fortune III; Looting III; Unbreaking III; Silk Touch; Mending
                        Map.entry(BuiltInLootTables.STRONGHOLD_LIBRARY, new StructureBooks(
                                        0.55f, 0.16f,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(5), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(5), 3),
                                                        new BookSpec(Enchantments.POWER, lvl(5), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(5), 3),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(3), 2),
                                                        new BookSpec(Enchantments.LOOTING, lvl(3), 2),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1),
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // woodland_mansion: ... + Mending
                        Map.entry(BuiltInLootTables.WOODLAND_MANSION, new StructureBooks(
                                        0.40f, 0.10f,
                                        List.of(
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(5), 3),
                                                        new BookSpec(Enchantments.SMITE, lvl(5), 2),
                                                        new BookSpec(Enchantments.LOOTING, lvl(3), 2),
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.THORNS, lvl(3), 2),
                                                        new BookSpec(Enchantments.POWER, lvl(5), 3),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1)))),

                        // bastions: Soul Speed II–III; Fire Prot IV; Sharpness IV; Looting II; Lunge II
                        // (Lunge resource key name may differ in your mappings; replace
                        // Enchantments.LUNGE with the correct constant.)
                        Map.entry(BuiltInLootTables.BASTION_BRIDGE, new StructureBooks(
                                        0.36f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.SOUL_SPEED, lvl(2, 3), 2),
                                                        new BookSpec(Enchantments.FIRE_PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.LUNGE, lvl(2), 2)),
                                        List.of())),
                        Map.entry(BuiltInLootTables.BASTION_OTHER, new StructureBooks(
                                        0.36f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.SOUL_SPEED, lvl(2, 3), 2),
                                                        new BookSpec(Enchantments.FIRE_PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.LUNGE, lvl(2), 2)),
                                        List.of())),
                        Map.entry(BuiltInLootTables.BASTION_HOGLIN_STABLE, new StructureBooks(
                                        0.36f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.SOUL_SPEED, lvl(2, 3), 2),
                                                        new BookSpec(Enchantments.FIRE_PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(4), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(2), 2),
                                                        new BookSpec(Enchantments.LUNGE, lvl(2), 2)),
                                        List.of())),

                        // bastion_treasure: Soul Speed III; Sharpness V; Looting III; Fortune III;
                        // Efficiency V; Unbreaking III; Silk Touch; Mending; Lunge III
                        Map.entry(BuiltInLootTables.BASTION_TREASURE, new StructureBooks(
                                        0.52f, 0.18f,
                                        List.of(
                                                        new BookSpec(Enchantments.SOUL_SPEED, lvl(3), 2),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(5), 3),
                                                        new BookSpec(Enchantments.LOOTING, lvl(3), 2),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(3), 2),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(5), 3),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1),
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1),
                                                        new BookSpec(Enchantments.LUNGE, lvl(3), 1)))),

                        // ancient_city_ice_box: Swift Sneak II; Protection IV; Efficiency V; Wind Burst
                        // II; Density IV
                        Map.entry(BuiltInLootTables.ANCIENT_CITY_ICE_BOX, new StructureBooks(
                                        0.44f, 0.00f,
                                        List.of(
                                                        new BookSpec(Enchantments.SWIFT_SNEAK, lvl(2), 2),
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(5), 3),
                                                        new BookSpec(Enchantments.WIND_BURST, lvl(2), 2),
                                                        new BookSpec(Enchantments.DENSITY, lvl(4), 2)),
                                        List.of())),

                        // ancient_city: Swift Sneak III; Protection IV; Efficiency V; Wind Burst III;
                        // Density V; Breach IV; Mending; Lunge III
                        Map.entry(BuiltInLootTables.ANCIENT_CITY, new StructureBooks(
                                        0.55f, 0.16f,
                                        List.of(
                                                        new BookSpec(Enchantments.SWIFT_SNEAK, lvl(3), 2),
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(5), 3),
                                                        new BookSpec(Enchantments.WIND_BURST, lvl(3), 2),
                                                        new BookSpec(Enchantments.DENSITY, lvl(5), 2),
                                                        new BookSpec(Enchantments.BREACH, lvl(4), 2)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1),
                                                        new BookSpec(Enchantments.LUNGE, lvl(3), 1)))),

                        // end_city_treasure: Protection IV; Sharpness V; Power V; Efficiency V; Fortune
                        // III; Looting III; Unbreaking III; Mending; Silk Touch
                        Map.entry(BuiltInLootTables.END_CITY_TREASURE, new StructureBooks(
                                        0.52f, 0.18f,
                                        List.of(
                                                        new BookSpec(Enchantments.PROTECTION, lvl(4), 3),
                                                        new BookSpec(Enchantments.SHARPNESS, lvl(5), 3),
                                                        new BookSpec(Enchantments.POWER, lvl(5), 3),
                                                        new BookSpec(Enchantments.EFFICIENCY, lvl(5), 3),
                                                        new BookSpec(Enchantments.FORTUNE, lvl(3), 2),
                                                        new BookSpec(Enchantments.LOOTING, lvl(3), 2),
                                                        new BookSpec(Enchantments.UNBREAKING, lvl(3), 3)),
                                        List.of(
                                                        new BookSpec(Enchantments.MENDING, lvl(1), 1),
                                                        new BookSpec(Enchantments.SILK_TOUCH, lvl(1), 1)))));
}
