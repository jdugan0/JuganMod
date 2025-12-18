package com.justindugan.juganmod.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public abstract class ItemStackBaneCleanseMixin {

  private static final List<Holder<MobEffect>> CLEANSE_ORDER = List.of(
      MobEffects.POISON,
      MobEffects.WITHER,
      MobEffects.HUNGER,
      MobEffects.WEAKNESS,
      MobEffects.SLOWNESS,
      MobEffects.BLINDNESS,
      MobEffects.MINING_FATIGUE,
      MobEffects.NAUSEA);

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void jugan$baneRightClickCleanses(Level level, Player player, InteractionHand hand,
      CallbackInfoReturnable<InteractionResult> cir) {

    if (!(level instanceof ServerLevel serverLevel))
      return;

    ItemStack self = (ItemStack) (Object) this;
    ItemEnchantments ench = self.getEnchantments();

    Holder.Reference<Enchantment> bane = serverLevel.registryAccess()
        .lookupOrThrow(Registries.ENCHANTMENT)
        .getOrThrow(Enchantments.BANE_OF_ARTHROPODS);

    int baneLevel = ench.getLevel(bane);
    if (baneLevel <= 0)
      return;

    int maxRemove = baneLevel;
    int removed = 0;

    for (Holder<MobEffect> eff : CLEANSE_ORDER) {
      if (removed >= maxRemove)
        break;
      if (player.hasEffect(eff)) {
        player.removeEffect(eff);
        removed++;
      }
    }

    if (removed == 0)
      return;

    int durabilityCost = 6 + 4 * baneLevel + 3 * baneLevel * baneLevel;
    int cooldownTicks = clamp(80 - 10 * baneLevel, 30, 80);

    self.hurtAndBreak(durabilityCost, player, hand);
    player.getCooldowns().addCooldown(self, cooldownTicks);
    player.swing(hand, true);

    cir.setReturnValue(InteractionResult.SUCCESS);
  }

  private static int clamp(int v, int lo, int hi) {
    return Math.max(lo, Math.min(hi, v));
  }
}
