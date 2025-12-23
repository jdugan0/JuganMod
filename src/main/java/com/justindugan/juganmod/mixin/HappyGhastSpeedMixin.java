package com.justindugan.juganmod.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HappyGhast.class)
public abstract class HappyGhastSpeedMixin {

    private static final double NEW_FLYING_SPEED = 0.1;
    private static final double NEW_MOVEMENT_SPEED = 0.1;

    @Inject(method = "createAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;", at = @At("RETURN"), cancellable = true)
    private static void jugan$boostSpeeds(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder b = cir.getReturnValue();
        b.add(Attributes.FLYING_SPEED, NEW_FLYING_SPEED);
        b.add(Attributes.MOVEMENT_SPEED, NEW_MOVEMENT_SPEED);
        cir.setReturnValue(b);
    }
}
