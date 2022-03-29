package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface CreatableItemStack {
    @NotNull ItemStack createItemStack();
}
