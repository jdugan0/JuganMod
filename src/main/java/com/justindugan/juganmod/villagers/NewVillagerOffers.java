package com.justindugan.juganmod.villagers;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public class NewVillagerOffers {
    public static void init() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((level, entity, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 3), new ItemStack(Items.LAPIS_LAZULI, 5), 7, 0, 0.04f));
        });
    }
}
