package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemComparable {
    boolean equalsItemType(@Nullable ItemStack itemStack);
}
