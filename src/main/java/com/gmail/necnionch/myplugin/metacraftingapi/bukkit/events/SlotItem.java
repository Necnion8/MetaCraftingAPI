package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SlotItem {
    private final @Nullable ItemStack itemStack;
    private final @Nullable CustomItem customItem;

    public SlotItem(@Nullable ItemStack itemStack, @Nullable CustomItem customItem) {
        this.itemStack = itemStack;
        this.customItem = customItem;
    }

    public @Nullable ItemStack getItemStack() {
        return itemStack;
    }

    public @Nullable CustomItem getCustomItem() {
        return customItem;
    }

}
