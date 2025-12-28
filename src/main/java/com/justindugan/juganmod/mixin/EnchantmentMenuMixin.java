package com.justindugan.juganmod.mixin;

import java.util.List;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.EnchantCapRules;
import com.justindugan.juganmod.enchants.EnchantTableRules;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
            int chance = (cost - 10) * 5;
            if (this.random.nextInt(100) < chance) {
                return ei;
            }
            if (ei.level() <= max)
                return ei;
            return new EnchantmentInstance(ei.enchantment(), max);
        }).toList();

        filtered = java.util.stream.Stream.concat(filtered.stream(),
                net.minecraft.world.item.enchantment.EnchantmentHelper
                        .selectEnchantment(this.random, stack, cost,
                                registryAccess.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                                        .listElements()
                                        .map(h -> (net.minecraft.core.Holder<Enchantment>) (Object) h)

                        )
                        .stream()
                        .filter(ei -> original.stream().noneMatch(x -> x.enchantment().equals(ei.enchantment())))
                        .limit(1))
                .toList();

        cir.setReturnValue(filtered);
    }
}
