package com.justindugan.juganmod;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static net.minecraft.commands.Commands.literal;

public final class JuganCommands {
    private JuganCommands() {}

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("jugan")
                .then(literal("markers")
                .requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .executes(ctx -> {
                        CommandSourceStack src = ctx.getSource();
                        ServerPlayer player = src.getPlayerOrException();

                        // Death
                        PlayerMarkers.Marker death = PlayerMarkers.getLastDeath(player);
                        if (death == null) {
                            src.sendSystemMessage(Component.literal("Last death: <none>")
                                    .withStyle(ChatFormatting.GRAY));
                        } else {
                            src.sendSystemMessage(formatMarker("Last death", death));
                        }

                        // Respawn
                        Optional<PlayerMarkers.Marker> respawn = PlayerMarkers.getRespawnMarker(player);
                        if (respawn.isEmpty()) {
                            src.sendSystemMessage(Component.literal("Respawn: <none>")
                                    .withStyle(ChatFormatting.GRAY));
                        } else {
                            src.sendSystemMessage(formatMarker("Respawn", respawn.get()));
                        }

                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
        });
    }

    private static Component formatMarker(String label, PlayerMarkers.Marker m) {
        String dim = dimString(m.dimension());
        String pos = String.format("%d %d %d", m.pos().getX(), m.pos().getY(), m.pos().getZ());
        return Component.literal(label + ": " + dim + " @ " + pos + " (t=" + m.gameTime() + ")")
                .withStyle(ChatFormatting.YELLOW);
    }

    private static String dimString(ResourceKey<Level> dim) {
        return dim.toString();
    }
}
