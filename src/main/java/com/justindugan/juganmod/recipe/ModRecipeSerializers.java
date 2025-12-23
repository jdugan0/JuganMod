package com.justindugan.juganmod.recipe;

import com.justindugan.juganmod.JuganMod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {

    public static final RecipeSerializer<DuplicateLodestoneRecipe> DUPLICATE_LODESTONE = new CustomRecipe.Serializer<>(
            DuplicateLodestoneRecipe::new);

    public static void init() {
        Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(JuganMod.MOD_ID, "lodestone_duplicate"),
                DUPLICATE_LODESTONE);
    }
}
