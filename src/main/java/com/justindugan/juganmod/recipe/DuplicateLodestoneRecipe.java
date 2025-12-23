package com.justindugan.juganmod.recipe;

import java.util.logging.Logger;

import com.justindugan.juganmod.JuganMod;
import com.justindugan.juganmod.ModDataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DuplicateLodestoneRecipe extends CustomRecipe {

    public DuplicateLodestoneRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        if (craftingInput.ingredientCount() != 2) {
            JuganMod.LOGGER.info("not enough items");
            return false;
        } else {
            ItemStack lodestone = null;
            boolean foundBlank = false;
            for (int i = 0; i < craftingInput.size(); i++) {
                ItemStack itemStack = craftingInput.getItem(i);
                if (lodestone != null && foundBlank) {
                    JuganMod.LOGGER.info("matched!");
                    return true;
                }
                if (!itemStack.isEmpty()) {
                    if (!(itemStack.getItem() instanceof CompassItem)) {
                        JuganMod.LOGGER.info("not a compass!");
                        return false;
                    }
                    if (itemStack.has(ModDataComponents.COMPASS_MODE)) {
                        foundBlank = true;
                        continue;
                    }
                    lodestone = itemStack;
                }
            }
            if (lodestone != null && foundBlank) {
                JuganMod.LOGGER.info("matched!");
                return true;
            }
            JuganMod.LOGGER.info("Lodestone: " + (lodestone != null) + " Blank: " + foundBlank);
            return false;

        }

    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        for (int i = 0; i < craftingInput.size(); i++) {
            ItemStack itemStack = craftingInput.getItem(i);
            if (!itemStack.isEmpty() && !itemStack.has(ModDataComponents.COMPASS_MODE)) {
                JuganMod.LOGGER.info("assembled!");
                return itemStack.copyWithCount(2);
            }
        }
        JuganMod.LOGGER.info("Failed assembly :(");
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
        NonNullList<ItemStack> nonNullList = NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
        return nonNullList;
    }

    @Override
    public RecipeSerializer<DuplicateLodestoneRecipe> getSerializer() {
        return ModRecipeSerializers.DUPLICATE_LODESTONE;
    }
}
