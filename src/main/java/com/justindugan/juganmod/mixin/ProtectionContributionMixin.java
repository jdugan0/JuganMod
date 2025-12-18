package com.justindugan.juganmod.mixin;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(Enchantment.class)
public abstract class ProtectionContributionMixin {
  private static final float PROT_SCALE = 0.75f;
  private static final float SPECIALTY_WITH_PROT_K = 0.35f;

  @Unique
  private static final ThreadLocal<Float> jugan$before = ThreadLocal.withInitial(() -> 0.0f);

  @Unique private static final int TYPE_NONE = 0;
  @Unique private static final int TYPE_PROT = 1;
  @Unique private static final int TYPE_FIRE = 2;
  @Unique private static final int TYPE_BLAST = 3;
  @Unique private static final int TYPE_PROJ = 4;

  @Unique
  private static int jugan$type(ServerLevel level, Enchantment self) {
    var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

    if (reg.getOrThrow(Enchantments.PROTECTION).value() == self) return TYPE_PROT;
    if (reg.getOrThrow(Enchantments.FIRE_PROTECTION).value() == self) return TYPE_FIRE;
    if (reg.getOrThrow(Enchantments.BLAST_PROTECTION).value() == self) return TYPE_BLAST;
    if (reg.getOrThrow(Enchantments.PROJECTILE_PROTECTION).value() == self) return TYPE_PROJ;

    return TYPE_NONE;
  }

  @Unique
  private static Holder.Reference<Enchantment> jugan$holder(ServerLevel level, int type) {
    var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
    return switch (type) {
      case TYPE_PROT -> reg.getOrThrow(Enchantments.PROTECTION);
      case TYPE_FIRE -> reg.getOrThrow(Enchantments.FIRE_PROTECTION);
      case TYPE_BLAST -> reg.getOrThrow(Enchantments.BLAST_PROTECTION);
      case TYPE_PROJ -> reg.getOrThrow(Enchantments.PROJECTILE_PROTECTION);
      default -> null;
    };
  }

  @Unique
  private static int jugan$levelOn(ItemStack stack, Holder.Reference<Enchantment> ench) {
    if (ench == null) return 0;
    ItemEnchantments e = stack.get(DataComponents.ENCHANTMENTS);
    return e == null ? 0 : e.getLevel(ench);
  }

  @Unique
  private static boolean jugan$hasAny(LivingEntity le, Holder.Reference<Enchantment> ench) {
    for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
      if (jugan$levelOn(le.getItemBySlot(slot), ench) > 0) return true;
    }
    return false;
  }

  @Unique
  private static int jugan$countPieces(LivingEntity le, Holder.Reference<Enchantment> ench) {
    int c = 0;
    for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
      if (jugan$levelOn(le.getItemBySlot(slot), ench) > 0) c++;
    }
    return c;
  }

  @Inject(
      method = "modifyDamageProtection(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lorg/apache/commons/lang3/mutable/MutableFloat;)V",
      at = @At("HEAD")
  )
  private void jugan$storeBefore(
      ServerLevel level, int enchLevel, ItemStack stack, Entity entity, DamageSource source, MutableFloat protection,
      CallbackInfo ci
  ) {
    int type = jugan$type(level, (Enchantment)(Object)this);
    if (type != TYPE_NONE) {
      jugan$before.set(protection.floatValue());
    }
  }

  @Inject(
      method = "modifyDamageProtection(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lorg/apache/commons/lang3/mutable/MutableFloat;)V",
      at = @At("RETURN")
  )
  private void jugan$scaleDelta(
      ServerLevel level, int enchLevel, ItemStack stack, Entity entity, DamageSource source, MutableFloat protection,
      CallbackInfo ci
  ) {
    int type = jugan$type(level, (Enchantment)(Object)this);
    if (type == TYPE_NONE) return;

    float before = jugan$before.get();
    float after = protection.floatValue();
    float delta = after - before;

    if (type == TYPE_PROT) {
      protection.setValue(before + delta * PROT_SCALE);
      jugan$before.remove();
      return;
    }

    if (!(entity instanceof LivingEntity le)) {
      jugan$before.remove();
      return;
    }

    Holder.Reference<Enchantment> prot = jugan$holder(level, TYPE_PROT);
    Holder.Reference<Enchantment> spec = jugan$holder(level, type);

    boolean hasProt = jugan$hasAny(le, prot);
    int count = Math.max(1, jugan$countPieces(le, spec));

    float k = hasProt ? SPECIALTY_WITH_PROT_K : 1.0f;
    float scale = (float)(k / Math.sqrt(count));

    protection.setValue(before + delta * scale);
    jugan$before.remove();
  }
}
