package com.justindugan.juganmod;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class LocatorUI {
    private static final Identifier LOCATOR_OFFLINE_ICON = Identifier.fromNamespaceAndPath(JuganMod.MOD_ID,
            "textures/gui/nocompass.png");

    public static void init() {

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, "locator_status"),
                (ctx, tickDelta) -> {
                    var mc = Minecraft.getInstance();
                    var player = mc.player;

                    if (player == null)
                        return;

                    double online = player.getAttributeValue(JuganModAttributes.LOCATOR_ONLINE);
                    if (online > 0.0)
                        return;
                    int size = 16;
                    int x = 10;
                    int y = mc.getWindow().getGuiScaledHeight() - 30;

                    ctx.blit(RenderPipelines.GUI_TEXTURED, LOCATOR_OFFLINE_ICON, x, y, 0, 0, size, size, size, size);
                });
    }
}
