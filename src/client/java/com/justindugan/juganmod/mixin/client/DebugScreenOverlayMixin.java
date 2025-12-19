package com.justindugan.juganmod.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.justindugan.juganmod.JuganMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.player.Player;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderLines", at = @At("HEAD"))
    private void juganmod_addYToReducedDebug(GuiGraphics guiGraphics, List<String> list, boolean left,
            CallbackInfo ci) {
        if (!left)
            return;
        Player p = this.minecraft.player;
        if (p == null)
            return;

        for (String s : list) {
            if (s != null && s.startsWith("Y:"))
                return;
        }

        list.add(String.format("Y: %.2f", p.getY()));
    }
}
