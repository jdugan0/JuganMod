package com.justindugan.juganmod.enchants;

import com.justindugan.juganmod.JuganMod;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class EnchantToolTip {
    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, ctx, type, lines) -> {
            if (stack.isEmpty())
                return;

            int cap = EnchantCapRules.getCap(stack);
            if (cap <= 0)
                return;

            int n = EnchantCapRules.countItemEnchants(stack, false);
            int remaining = Math.max(0, cap - n);

            var color = (n >= cap) ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GRAY;
            lines.add(Component.literal("Enchants: " + n + "/" + cap + " (" + remaining + " remaining)")
                    .withStyle(color));
        });
    }
}