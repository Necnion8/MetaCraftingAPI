package com.gmail.necnionch.myplugin.metacraftingapi.bukkit;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.CustomItem;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.CustomRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;


public abstract class MetaCraftingAPI {
    private static MetaCraftingAPI instance = null;


    public static <I extends CustomItem> I registerCustomItem(Plugin owner, I customItem) {
        Objects.requireNonNull(instance, "API is unavailable").registerCustomItemImpl(owner, customItem);
        return customItem;
    }

    public static void unregisterCustomItem(CustomItem customItem) {
        Objects.requireNonNull(instance, "API is unavailable").unregisterCustomItemImpl(customItem);
    }

    public static void unregisterAllCustomItems(Plugin owner) {
        Objects.requireNonNull(instance, "API is unavailable").unregisterAllCustomItemsImpl(owner);
    }

    public static Collection<CustomItem> getCustomItems(@Nullable Plugin owner) {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomItemsImpl(owner);
    }

    public static Collection<CustomItem> getCustomItems() {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomItemsImpl(null);
    }

    public static @Nullable CustomItem getCustomItemByItemStack(ItemStack itemStack) {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomItemByItemStackImpl(itemStack);
    }

    public static @Nullable CustomItem getCustomItem(String itemId) {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomItemImpl(itemId);
    }

    public static void registerCustomRecipe(Plugin owner, CustomRecipe customRecipe) {
        Objects.requireNonNull(instance, "API is unavailable").registerCustomRecipeImpl(owner, customRecipe);
    }

    public static void unregisterCustomRecipe(CustomRecipe customRecipe) {
        Objects.requireNonNull(instance, "API is unavailable").unregisterCustomRecipeImpl(customRecipe);
    }

    public static void unregisterAllCustomRecipes(Plugin owner) {
        Objects.requireNonNull(instance, "API is unavailable").unregisterAllCustomRecipesImpl(owner);
    }

    public static Collection<CustomRecipe> getCustomRecipes(@Nullable Plugin owner) {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomRecipesImpl(owner);
    }

    public static Collection<CustomRecipe> getCustomRecipes() {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomRecipesImpl(null);
    }

    public static @Nullable CustomRecipe getCustomRecipe(NamespacedKey recipeKey) {
        return Objects.requireNonNull(instance, "API is unavailable").getCustomRecipeImpl(recipeKey);
    }

    public static void unregisterBy(Plugin owner) {
        Objects.requireNonNull(instance, "API is unavailable");
        unregisterAllCustomRecipes(owner);
        unregisterAllCustomItems(owner);
    }



    protected abstract void registerCustomItemImpl(Plugin owner, CustomItem customItem);

    protected abstract void unregisterCustomItemImpl(CustomItem customItem);

    protected abstract void unregisterAllCustomItemsImpl(Plugin owner);

    protected abstract Collection<CustomItem> getCustomItemsImpl(@Nullable Plugin owner);

    @Nullable
    protected abstract CustomItem getCustomItemByItemStackImpl(ItemStack itemStack);

    protected abstract CustomItem getCustomItemImpl(String itemId);

    protected abstract void registerCustomRecipeImpl(Plugin owner, CustomRecipe customRecipe);

    protected abstract void unregisterCustomRecipeImpl(CustomRecipe customRecipe);

    protected abstract void unregisterAllCustomRecipesImpl(Plugin owner);

    protected abstract Collection<CustomRecipe> getCustomRecipesImpl(@Nullable Plugin owner);

    protected abstract @Nullable CustomRecipe getCustomRecipeImpl(NamespacedKey recipeKey);


}
