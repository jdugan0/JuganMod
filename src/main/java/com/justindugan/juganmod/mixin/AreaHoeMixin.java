package com.justindugan.juganmod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.enchants.JuganModEnchantmentEffects;
import com.mojang.datafixers.util.Pair;

@Mixin(HoeItem.class)
public abstract class AreaHoeMixin {
    @Accessor("TILLABLES")
    static Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> jugan$getTillables() {
        throw new AssertionError();
    }

    @Unique
    private static final ThreadLocal<Boolean> jugan$reentry = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "useOn", at = @At("RETURN"))
    private void jugan$areaHoe(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.SUCCESS)
            return;

        Level level = ctx.getLevel();
        if (level.isClientSide())
            return;
        if (Boolean.TRUE.equals(jugan$reentry.get()))
            return;

        Player player = ctx.getPlayer();
        if (player == null)
            return;

        ItemStack tool = ctx.getItemInHand();

        BlockPos center = ctx.getClickedPos();
        Direction face = ctx.getClickedFace();

        jugan$reentry.set(Boolean.TRUE);
        try {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;

                    BlockPos p = center.offset(dx, 0, dz);
                    var hit = new net.minecraft.world.phys.BlockHitResult(
                            net.minecraft.world.phys.Vec3.atCenterOf(p),
                            face,
                            p,
                            false);
                    UseOnContext nctx = new UseOnContext(player, ctx.getHand(), hit);
                    var pair = AreaHoeMixin.jugan$getTillables().get(level.getBlockState(p).getBlock());
                    if (pair == null)
                        continue;

                    if (!pair.getFirst().test(nctx))
                        continue;
                    pair.getSecond().accept(nctx);

                    tool.hurtAndBreak(1, player, ctx.getHand().asEquipmentSlot());
                }
            }
        } finally {
            jugan$reentry.set(Boolean.FALSE);
        }
    }
}
