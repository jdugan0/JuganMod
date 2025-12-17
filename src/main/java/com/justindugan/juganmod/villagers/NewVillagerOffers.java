package com.justindugan.juganmod.villagers;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.WanderingTraderOffersBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public final class NewVillagerOffers {

    private record EnchantSpec(ResourceKey<Enchantment> key, int minLevel, int maxLevelExclusive, int baseMin,
            int baseMax, int perLevelMin, int perLevelMax) {
    }

    private static final List<EnchantSpec> LIB_T4 = List.of(
            spec(Enchantments.UNBREAKING, 1, 3, 12, 18, 3, 5),
            spec(Enchantments.EFFICIENCY, 1, 4, 14, 20, 4, 6),
            spec(Enchantments.SHARPNESS, 1, 4, 14, 20, 4, 6),
            spec(Enchantments.PROTECTION, 1, 4, 14, 20, 4, 6),
            spec(Enchantments.POWER, 1, 4, 14, 20, 4, 6),
            spec(Enchantments.FEATHER_FALLING, 1, 4, 12, 18, 3, 5),
            spec(Enchantments.FIRE_PROTECTION, 1, 4, 11, 17, 3, 5),
            spec(Enchantments.PROJECTILE_PROTECTION, 1, 4, 11, 17, 3, 5),
            spec(Enchantments.BLAST_PROTECTION, 1, 4, 11, 17, 3, 5),
            spec(Enchantments.RESPIRATION, 1, 3, 13, 19, 4, 6),
            spec(Enchantments.AQUA_AFFINITY, 1, 2, 14, 20, 0, 0),
            spec(Enchantments.DEPTH_STRIDER, 1, 3, 13, 19, 4, 6),
            spec(Enchantments.LURE, 1, 3, 10, 16, 3, 5),
            spec(Enchantments.LUCK_OF_THE_SEA, 1, 3, 12, 18, 3, 5),
            spec(Enchantments.PIERCING, 1, 4, 11, 17, 3, 5),
            spec(Enchantments.QUICK_CHARGE, 1, 3, 12, 18, 4, 6),
            spec(Enchantments.LOYALTY, 1, 3, 13, 19, 4, 6),
            spec(Enchantments.IMPALING, 1, 4, 11, 17, 3, 5));

    private static final List<EnchantSpec> LIB_T5 = List.of(
            spec(Enchantments.EFFICIENCY, 2, 5, 18, 26, 4, 7),
            spec(Enchantments.SHARPNESS, 2, 5, 18, 26, 4, 7),
            spec(Enchantments.PROTECTION, 2, 5, 18, 26, 4, 7),
            spec(Enchantments.POWER, 2, 5, 18, 26, 4, 7),
            spec(Enchantments.FORTUNE, 1, 3, 20, 30, 6, 9),
            spec(Enchantments.THORNS, 1, 3, 20, 30, 6, 9),
            spec(Enchantments.LOOTING, 1, 3, 20, 30, 6, 9),
            spec(Enchantments.SWEEPING_EDGE, 1, 3, 18, 28, 6, 9),
            spec(Enchantments.MULTISHOT, 1, 2, 22, 32, 0, 0),
            spec(Enchantments.CHANNELING, 1, 2, 24, 36, 0, 0),
            spec(Enchantments.RIPTIDE, 1, 3, 18, 28, 6, 9),
            spec(Enchantments.INFINITY, 1, 2, 26, 40, 0, 0),
            spec(Enchantments.LUNGE, 1, 3, 26, 40, 6, 9));

    private static final List<EnchantSpec> WANDERING_RARE = List.of(
            spec(Enchantments.MENDING, 1, 2, 34, 46, 0, 0),
            spec(Enchantments.SILK_TOUCH, 1, 2, 32, 44, 0, 0),
            spec(Enchantments.SWIFT_SNEAK, 1, 3, 36, 50, 6, 9),
            spec(Enchantments.SOUL_SPEED, 1, 3, 34, 48, 6, 9),
            spec(Enchantments.SHARPNESS, 5, 6, 40, 52, 6, 9),
            spec(Enchantments.PROTECTION, 4, 5, 40, 52, 6, 9));

    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((level, entity, random) -> librarianOffer((ServerLevel) level, random, LIB_T4, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((level, entity, random) -> librarianOffer((ServerLevel) level, random, LIB_T5, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((level, entity, random) -> librarianOffer((ServerLevel) level, random, LIB_T4, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((level, entity, random) -> librarianOffer((ServerLevel) level, random, LIB_T5, 0.05f));
        });

        TradeOfferHelper.registerWanderingTraderOffers(factories -> {
            factories.addOffersToPool(WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL,
                    (level, entity, random) -> wanderingRareOffer((ServerLevel) level, random, 0.04f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((level, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3), new ItemStack(Items.LAPIS_LAZULI, 5), 7, 0, 0.04f));
        });

        TradeOfferHelper.registerWanderingTraderOffers(factories -> {
            factories.addOffersToPool(WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL,
                    (level, entity, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 45),
                            new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1), 1, 0, 0.04f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((lvl, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    EnchantmentHelper.enchantItem(
                            random,
                            new ItemStack(Items.BOOK),
                            5,
                            entity.level().registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .listElements()
                                    .map(h -> (Holder<Enchantment>) (Holder<?>) h)),
                    3, 10, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add((lvl, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    EnchantmentHelper.enchantItem(
                            random,
                            new ItemStack(Items.BOOK),
                            5,
                            entity.level().registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .listElements()
                                    .map(h -> (Holder<Enchantment>) (Holder<?>) h)),
                    3, 10, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            factories.add((lvl, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    EnchantmentHelper.enchantItem(
                            random,
                            new ItemStack(Items.BOOK),
                            10,
                            entity.level().registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .listElements()
                                    .map(h -> (Holder<Enchantment>) (Holder<?>) h)),
                    3, 10, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((lvl, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    EnchantmentHelper.enchantItem(
                            random,
                            new ItemStack(Items.BOOK),
                            10,
                            entity.level().registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .listElements()
                                    .map(h -> (Holder<Enchantment>) (Holder<?>) h)),
                    3, 10, 0.05f));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 5, factories -> {
            factories.add((lvl, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 24),
                    EnchantmentHelper.enchantItem(
                            random,
                            new ItemStack(Items.BOOK),
                            15,
                            entity.level().registryAccess()
                                    .lookupOrThrow(Registries.ENCHANTMENT)
                                    .listElements()
                                    .map(h -> (Holder<Enchantment>) (Holder<?>) h)),
                    3, 10, 0.05f));
        });

    }

    private static MerchantOffer librarianOffer(ServerLevel level, RandomSource random, List<EnchantSpec> pool,
            float priceMult) {
        EnchantSpec spec = pool.get(random.nextInt(pool.size()));
        Holder<Enchantment> ench = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(spec.key());
        int chosenLevel = chooseLevel(random, spec.minLevel(), spec.maxLevelExclusive() - 1);
        int emeraldCost = computeCost(random, spec, chosenLevel);

        return new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                Optional.of(new ItemCost(Items.BOOK, 1)),
                EnchantmentHelper.createBook(new EnchantmentInstance(ench, chosenLevel)),
                randBetween(random, 2, 5),
                10,
                priceMult);
    }

    private static MerchantOffer wanderingRareOffer(ServerLevel level, RandomSource random, float priceMult) {
        EnchantSpec spec = WANDERING_RARE.get(random.nextInt(WANDERING_RARE.size()));
        Holder<Enchantment> ench = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(spec.key());
        int chosenLevel = chooseLevel(random, spec.minLevel(), spec.maxLevelExclusive() - 1);

        int emeraldCost = computeCost(random, spec, chosenLevel) + randBetween(random, 6, 14);
        int diamondCost = randBetween(random, 3, 7);

        return new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                Optional.of(new ItemCost(Items.DIAMOND, diamondCost)),
                EnchantmentHelper.createBook(new EnchantmentInstance(ench, chosenLevel)),
                1,
                0,
                priceMult);
    }

    private static int computeCost(RandomSource random, EnchantSpec spec, int level) {
        int base = randBetween(random, spec.baseMin(), spec.baseMax());
        int extra = (level - 1) * randBetween(random, spec.perLevelMin(), spec.perLevelMax());
        float jitter = 0.90f + random.nextFloat() * 0.20f;
        return Math.max(1, Math.round((base + extra) * jitter));
    }

    private static int chooseLevel(RandomSource random, int min, int max) {
        if (max <= min)
            return min;
        return randBetween(random, min, max);
    }

    private static int randBetween(RandomSource random, int min, int max) {
        if (max <= min)
            return min;
        return min + random.nextInt(max - min + 1);
    }

    private static EnchantSpec spec(ResourceKey<Enchantment> key, int minLevel, int maxLevelExclusive,
            int baseMin, int baseMax, int perLevelMin, int perLevelMax) {
        return new EnchantSpec(key, minLevel, maxLevelExclusive, baseMin, baseMax, perLevelMin, perLevelMax);
    }
}
