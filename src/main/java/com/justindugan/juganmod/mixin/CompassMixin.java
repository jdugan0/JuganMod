package com.justindugan.juganmod.mixin;

import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.justindugan.juganmod.ModDataComponents;
import com.justindugan.juganmod.spawns.CustomSpawns;
import com.justindugan.juganmod.spawns.SavedSpawns;

@Mixin(Item.class)
public class CompassMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void compassCycle(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player.getItemInHand(hand).getItem() instanceof CompassItem))
            return;
        if (level.isClientSide())
            return;
        ItemStack stack = player.getItemInHand(hand);

        int mode = stack.getOrDefault(ModDataComponents.COMPASS_MODE, 0);
        mode = (mode + 1) % 4;
        stack.set(ModDataComponents.COMPASS_MODE, mode);

        System.out.println("Compass is now pointing towards: " + mode);

        // Update the lodestone tracker to point to the new target
        BlockPos target = CustomSpawns.SPAWNS.get(mode);
        LodestoneTracker tracker = new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), target)), false);
        stack.set(DataComponents.LODESTONE_TRACKER, tracker);

        int color = 0x00FF00;
        switch (mode) {
            case 0:
                color = 0x00FF00;
                break;
            case 1:
                color = 0xFFFF00;
                break;
            case 2:
                color = 0xFF0000;
                break;
            case 3:
                color = 0x6666FF;
                break;
        }

        Component actionBar = Component.literal("Compass now pointing to Spawn " + (mode + 1))
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
        player.displayClientMessage(actionBar, true);

    }

    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot, boolean selected, CallbackInfo ci)  {
        {
            if (!(entity instanceof Player player))
                return;
            player = (Player) entity;

            if (!(itemStack.getItem() instanceof CompassItem))
                return;
            if (serverLevel.isClientSide())
                return;
            ItemStack stack2 = itemStack;
            if (stack2.has(DataComponents.LODESTONE_TRACKER))
                return;

            SavedSpawns save = SavedSpawns.get(player.level().getServer());
            int mode = CustomSpawns.SPAWNS.indexOf(save.getSpawn(player.getUUID()));
            stack2.set(ModDataComponents.COMPASS_MODE, mode);

            System.out.println("Compass is now pointing towards: " + mode);

            // Update the lodestone tracker to point to the new target
            BlockPos target = CustomSpawns.SPAWNS.get(mode);
            LodestoneTracker tracker = new LodestoneTracker(Optional.of(GlobalPos.of(serverLevel.dimension(), target)),
                    false);
            stack2.set(DataComponents.LODESTONE_TRACKER, tracker);

            int color = 0x00FF00;
            switch (mode) {
                case 0:
                    color = 0x00FF00;
                    break;
                case 1:
                    color = 0xFFFF00;
                    break;
                case 2:
                    color = 0xFF0000;
                    break;
                case 3:
                    color = 0x6666FF;
                    break;
            }

            Component actionBar = Component.literal("Compass now pointing to Spawn " + (mode + 1))
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
            player.displayClientMessage(actionBar, true);
        }
    }

}