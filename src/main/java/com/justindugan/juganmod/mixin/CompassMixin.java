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

        if (!stack.has(ModDataComponents.COMPASS_MODE))
            return;

        Integer mode = stack.get(ModDataComponents.COMPASS_MODE);
        if (mode == null)
            return;
        mode = (mode + 1) % 5;
        stack.set(ModDataComponents.COMPASS_MODE, mode);

        System.out.println("Compass is now pointing towards: " + mode);

        // Update the lodestone tracker to point to the new target
        BlockPos target = null;
        if (mode != 4) {
            target = CustomSpawns.SPAWNS.get(mode);
        } else {
            target = new BlockPos(0, 0, 0);
        }
        LodestoneTracker tracker = new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), target)), false);
        stack.set(DataComponents.LODESTONE_TRACKER, tracker);

        int color = 0x00FF00;
        String name = "";
        switch (mode) {
            case 0:
                color = 0xFFC5D3;
                name = "Cherry Shrine";
                break;
            case 1:
                color = 0x40FFa9;
                name = "Copper Shrine";
                break;
            case 2:
                color = 0xff8936;
                name = "Magma Shrine";
                break;
            case 3:
                color = 0x5498ff;
                name = "Aqua Shrine";
                break;
            case 4:
                color = 0xc2c2c2;
                name = "the Hub";
                break;
        }

        Component actionBar = Component.literal("Compass now pointing to " + name)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
        player.displayClientMessage(actionBar, true);

    }
}