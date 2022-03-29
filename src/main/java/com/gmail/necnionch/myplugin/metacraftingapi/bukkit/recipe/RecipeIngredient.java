package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.ItemComparable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public interface RecipeIngredient extends ItemComparable {
    @NotNull Material getRecipeMaterial();
}
