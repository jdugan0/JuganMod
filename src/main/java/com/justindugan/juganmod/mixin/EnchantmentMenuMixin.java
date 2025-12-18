package com.justindugan.juganmod.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.EnchantCapRules;
import com.justindugan.juganmod.enchants.EnchantTableRules;

import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow
    @Final
    private RandomSource random;

    @Inject(method = "getEnchantmentList", at = @At("RETURN"), cancellable = true)
    private void jugan$filterEnchantmentTableRoll(
            RegistryAccess registryAccess, ItemStack stack, int option, int cost,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir) {

        List<EnchantmentInstance> original = cir.getReturnValue();
        if (original.isEmpty())
            return;
        List<EnchantmentInstance> filtered = original.stream().map(ei -> {
            int max = EnchantTableRules.maxTableLevel(ei.enchantment());
            int chance = 15 + (ei.level() - 25) * 5;
            if (this.random.nextInt(100) < chance) {
                return ei;
            }
            if (ei.level() <= max)
                return ei;
            return new EnchantmentInstance(ei.enchantment(), max);
        }).toList();

        cir.setReturnValue(filtered);
    }
}
