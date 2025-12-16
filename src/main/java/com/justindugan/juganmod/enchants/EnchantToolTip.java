package com.justindugan.juganmod.enchants;

import com.justindugan.juganmod.JuganMod;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class EnchantToolTip {
    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, ctx, type, lines) -> {
            if (stack.isEmpty())
                return;

            int cap = EnchantCapRules.getCap(stack);
            int n = EnchantCapRules.countItemEnchants(stack, false);
            JuganMod.LOGGER.info("Enchants: " + n + " Cap: " + cap);
            if (cap > 0 && n > 0 && n < cap) {
                lines.add(Component.literal("Enchants: " + n + "/" + cap)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
            if (cap > 0 && n > 0 && n >= cap) {
                lines.add(Component.literal("Enchants: " + n + "/" + cap)
                        .withStyle(ChatFormatting.DARK_RED));
            }
        });

    }
}
