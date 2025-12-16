package com.justindugan.juganmod.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.EnchantCapRules;
import com.justindugan.juganmod.enchants.EnchantTableRules;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Inject(method = "getEnchantmentList", at = @At("RETURN"), cancellable = true)
    private void jugan$filterEnchantmentTableRoll(
            RegistryAccess registryAccess, ItemStack stack, int option, int cost,
            CallbackInfoReturnable<List<EnchantmentInstance>> cir) {

        List<EnchantmentInstance> original = cir.getReturnValue();
        if (original.isEmpty())
            return;

        int cap = EnchantCapRules.getCap(stack);
        if (cap <= 0)
            return;

        int already = EnchantCapRules.countItemEnchants(stack, false);
        int remaining = Math.max(0, cap - already);
        if (remaining == 0) {
            cir.setReturnValue(List.of());
            return;
        }
        List<EnchantmentInstance> filtered = original.stream()
                .filter(ei -> ei.level() <= EnchantTableRules.maxTableLevel(ei.enchantment()))
                .toList();

        if (filtered.isEmpty()) {
            cir.setReturnValue(List.of());
            return;
        }

        if (filtered.size() > remaining) {
            filtered = filtered.stream()
                    .sorted((a, b) -> Integer.compare(b.level(), a.level()))
                    .limit(remaining)
                    .toList();
        }

        cir.setReturnValue(filtered);
    }
}
