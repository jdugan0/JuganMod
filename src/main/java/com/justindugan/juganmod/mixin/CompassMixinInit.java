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

@Mixin(CompassItem.class)
public class CompassMixinInit {
    @Inject(method = "inventoryTick", at = @At("TAIL"))
    private void inventoryTick(ItemStack stack,
            ServerLevel level,
            Entity entity,
            @Nullable EquipmentSlot slot,
            CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;
        if (level.isClientSide())
            return;
        if (!(stack.getItem() instanceof CompassItem))
            return;
        if (stack.has(DataComponents.LODESTONE_TRACKER))
            return;

        SavedSpawns save = SavedSpawns.get(level.getServer());
        int mode = CustomSpawns.SPAWNS.indexOf(save.getSpawn(player.getUUID()));
        if (mode < 0)
            mode = 0;

        stack.set(ModDataComponents.COMPASS_MODE, mode);

        BlockPos target = CustomSpawns.SPAWNS.get(mode);
        stack.set(DataComponents.LODESTONE_TRACKER,
                new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), target)), false));
    }
}
