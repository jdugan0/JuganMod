package com.justindugan.juganmod.enchants.xp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class XpBonusContext {
    private XpBonusContext() {
    }

    public record Ctx(ServerPlayer player, ItemStack stack) {
    }

    private static final ThreadLocal<Ctx> CTX = new ThreadLocal<>();

    public static void push(ServerPlayer player, ItemStack stack) {
        CTX.set(new Ctx(player, stack));
    }

    public static void pop() {
        CTX.remove();
    }

    public static Ctx get() {
        return CTX.get();
    }
}
