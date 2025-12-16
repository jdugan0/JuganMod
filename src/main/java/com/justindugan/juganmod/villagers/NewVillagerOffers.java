package com.justindugan.juganmod.villagers;

import java.util.Optional;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.WanderingTraderOffersBuilder;
import net.fabricmc.fabric.impl.object.builder.TradeOfferInternals.WanderingTraderOffersBuilderImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;

public class NewVillagerOffers {
    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((level, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3), new ItemStack(Items.LAPIS_LAZULI, 5), 7, 0, 0.04f));
        });

        TradeOfferHelper.registerWanderingTraderOffers(factories -> {
            factories.addOffersToPool(
                    WanderingTraderOffersBuilder.SELL_SPECIAL_ITEMS_POOL,
                    (level, entity, random) -> {
                        Holder<Enchantment> mending = level.registryAccess()
                                .lookupOrThrow(Registries.ENCHANTMENT)
                                .getOrThrow(Enchantments.MENDING);
                        MerchantOffer m = new MerchantOffer(
                                new ItemCost(Items.EMERALD, 30),
                                Optional.of(new ItemCost(Items.DIAMOND, 5)),
                                EnchantmentHelper.createBook(new EnchantmentInstance(mending, 1)),
                                1,
                                0,
                                0.04f);
                        return m;
                    });
        });

    }
}
