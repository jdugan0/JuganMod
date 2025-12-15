package com.justindugan.juganmod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public final class WaypointRanges {
    private WaypointRanges() {
    }

    public static final double DAY_RECEIVE = 256.0;
    public static final double DAY_TRANSMIT = 256.0;
    public static final double NIGHT_RECEIVE = 0.0;
    public static final double NIGHT_TRANSMIT = 0.0;

    private static final Map<ResourceKey<Level>, Boolean> lastNight = new ConcurrentHashMap<>();

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            applyForPlayer(handler.player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            applyForPlayer(newPlayer);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, dest) -> {
            applyForPlayer(player);
        });

        ServerTickEvents.END_WORLD_TICK.register(level -> {
            boolean nowNight = DayNight.isNight(level);
            ResourceKey<Level> key = level.dimension();

            Boolean prev = lastNight.put(key, nowNight);
            if (prev != null && prev == nowNight)
                return;
            for (ServerPlayer p : level.players()) {
                applyForPlayer(p);
            }
        });
    }

    private static void applyForPlayer(ServerPlayer p) {
        boolean night = DayNight.isNight(p.level());
        double receive = night ? NIGHT_RECEIVE : DAY_RECEIVE;
        double transmit = night ? NIGHT_TRANSMIT : DAY_TRANSMIT;

        setBaseIfChanged(p, Attributes.WAYPOINT_RECEIVE_RANGE, receive);
        setBaseIfChanged(p, Attributes.WAYPOINT_TRANSMIT_RANGE, transmit);
        double val = p.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE);

        setBaseIfChanged(p, JuganModAttributes.LOCATOR_ONLINE, night ? 0.0 : 1.0);

        JuganMod.LOGGER.info("set ranges: " + val);
    }

    private static void setBaseIfChanged(ServerPlayer p, Holder<Attribute> attr, double value) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null)
            return;
        if (inst.getBaseValue() == value)
            return;
        inst.setBaseValue(value);
    }
}
