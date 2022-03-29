package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.MetaCraftingPlugin;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.RecipeIngredient;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class CustomItem implements CreatableItemStack, RecipeIngredient {

    public abstract @NotNull String getItemId();

    public abstract @NotNull ItemStack create();


    @Override
    public @NotNull ItemStack createItemStack() {
        ItemStack itemStack = create();
        setItemId(itemStack, getItemId());
        return itemStack;
    }

    public static void setItemId(@NotNull ItemStack itemStack, String itemId) {
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(MetaCraftingPlugin.getInstance(), "item"), PersistentDataType.STRING, itemId);
        itemStack.setItemMeta(meta);
    }

    public static boolean hasItemId(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                PersistentDataContainer data = meta.getPersistentDataContainer();
                return data.has(new NamespacedKey(MetaCraftingPlugin.getInstance(), "item"), PersistentDataType.STRING);
            }
        }
        return false;
    }

    public static @Nullable String getItemId(@Nullable ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                PersistentDataContainer data = meta.getPersistentDataContainer();
                return data.get(new NamespacedKey(MetaCraftingPlugin.getInstance(), "item"), PersistentDataType.STRING);
            }
        }
        return null;
    }

    @Override
    public boolean equalsItemType(@Nullable ItemStack itemStack) {
        return getItemId().equalsIgnoreCase(getItemId(itemStack));
    }


    public static CustomItem make(String itemId, Material material, Consumer<ItemStack> creator) {
        return new CustomItem() {
            @Override
            public @NotNull Material getRecipeMaterial() {
                return material;
            }

            @Override
            public @NotNull String getItemId() {
                return itemId;
            }

            @Override
            public @NotNull ItemStack create() {
                ItemStack itemStack = new ItemStack(material);
                creator.accept(itemStack);
                return itemStack;
            }
        };
    }

}
