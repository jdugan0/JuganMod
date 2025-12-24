package com.justindugan.juganmod.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityClimbSpeedMixin {
    @ModifyConstant(method = "handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;", constant = @Constant(doubleValue = 0.2D))
    private double jugan$climbBumpInAir(double original) {
        return 0.35D;
    }

    @ModifyConstant(method = "travelInWater(Lnet/minecraft/world/phys/Vec3;DZD)V", constant = @Constant(doubleValue = 0.2D))
    private double jugan$climbBumpInWater(double original) {
        return 0.35D;
    }

    @ModifyArg(method = "handleOnClimbable(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"), index = 1)
    private double jugan$climbDownFloor(double originalFloor) {
        return -0.35D;
    }
}
