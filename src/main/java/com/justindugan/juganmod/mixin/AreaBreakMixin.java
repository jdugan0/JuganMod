package com.justindugan.juganmod.mixin;
import com.justindugan.juganmod.enchants.JuganModEnchantmentEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.server.level.ServerPlayerGameMode.class)
public abstract class AreaBreakMixin {

    @Shadow
    protected ServerPlayer player;

    @Unique
    private static final ThreadLocal<Boolean> jugan$reentry = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void jugan$afterDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            return;
        if (Boolean.TRUE.equals(jugan$reentry.get()))
            return;

        Level level = player.level();
        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty())
            return;

        Holder<Enchantment> area = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(JuganModEnchantmentEffects.AREA);

        int areaLevel = EnchantmentHelper.getItemEnchantmentLevel(area, tool);
        if (areaLevel <= 0)
            return;

        var look = player.getLookAngle();
        double ax = Math.abs(look.x);
        double ay = Math.abs(look.y);
        double az = Math.abs(look.z);

        Direction.Axis axis;
        if (ay >= ax && ay >= az)
            axis = Direction.Axis.Y;
        else if (ax >= az)
            axis = Direction.Axis.X;
        else
            axis = Direction.Axis.Z;
        Direction u, v;
        if (axis == Direction.Axis.Y) {
            u = Direction.EAST;
            v = Direction.SOUTH;
        } else if (axis == Direction.Axis.X) {
            u = Direction.UP;
            v = Direction.SOUTH;
        } else {
            u = Direction.UP;
            v = Direction.EAST;
        }

        jugan$reentry.set(Boolean.TRUE);
        try {
            for (int du = -1; du <= 1; du++) {
                for (int dv = -1; dv <= 1; dv++) {
                    if (du == 0 && dv == 0)
                        continue;

                    BlockPos p = pos.relative(u, du).relative(v, dv);
                    player.gameMode.destroyBlock(p);
                }
            }
        } finally {
            jugan$reentry.set(Boolean.FALSE);
        }
    }
}
