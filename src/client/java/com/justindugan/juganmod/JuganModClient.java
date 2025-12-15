package com.justindugan.juganmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class JuganModClient implements ClientModInitializer {
	public static boolean locatorOffline = false;

	@Override
	public void onInitializeClient() {
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
					String text = "Locator offline (night)";
					int x = 10;
					int y = mc.getWindow().getGuiScaledHeight() - 30;
					ctx.drawString(mc.font, text, x, y, 0xFFA0A0A0, true);
				});
	}
}