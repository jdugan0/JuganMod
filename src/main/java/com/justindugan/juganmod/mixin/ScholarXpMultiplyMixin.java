// com.justindugan.juganmod.mixin/ScholarXpMultiplyMixin.java
package com.justindugan.juganmod.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.justindugan.juganmod.JuganMod;
import com.justindugan.juganmod.enchants.JuganModEnchantmentEffects;
import com.justindugan.juganmod.enchants.xp.XpBonusContext;

@Mixin(ExperienceOrb.class)
public abstract class ScholarXpMultiplyMixin {

    @ModifyVariable(
        method = "award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private static int jugan$modifyXp(int amount, ServerLevel level, Vec3 pos) {
        if (amount <= 0) return amount;

        var ctx = XpBonusContext.get();
        if (ctx == null) return amount;

        Holder<Enchantment> scholar = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(JuganModEnchantmentEffects.SCHOLAR);

        int lvl = EnchantmentHelper.getItemEnchantmentLevel(scholar, ctx.stack());
        if (lvl <= 0) return amount;
        double mult = 1.0 + 0.5 * lvl;

        int out = (int) Math.floor(amount * mult);
        return Math.max(1, out);
    }
}
