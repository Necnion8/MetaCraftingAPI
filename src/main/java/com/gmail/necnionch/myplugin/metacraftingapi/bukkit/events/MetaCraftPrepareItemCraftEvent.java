package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.CustomRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaCraftPrepareItemCraftEvent extends Event implements Cancellable {
    public static final HandlerList HANDLERS = new HandlerList();
    private final PrepareItemCraftEvent craftEvent;
    private final boolean dummyRecipe;
    private final @Nullable CustomRecipe matchRecipe;
    private @Nullable ItemStack result;
    private Result action;
    private boolean cancelled;


    public MetaCraftPrepareItemCraftEvent(PrepareItemCraftEvent baseEvent, @Nullable ItemStack resultItemStack, Result result, boolean dummyRecipe, @Nullable CustomRecipe matchRecipe) {
        craftEvent = baseEvent;
        this.result = resultItemStack;
        action = result;
        this.dummyRecipe = dummyRecipe;
        this.matchRecipe = matchRecipe;
    }

    public PrepareItemCraftEvent getCraftEvent() {
        return craftEvent;
    }

    public @Nullable ItemStack getResult() {
        return result;
    }

    public void setResult(@Nullable ItemStack result) {
        this.result = result;
    }

    public Result getAction() {
        return action;
    }

    public void setAction(Result action) {
        this.action = action;
    }

    public boolean isDummyRecipe() {
        return dummyRecipe;
    }

    public @Nullable CustomRecipe getMatchedCustomRecipe() {
        return matchRecipe;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public CraftingInventory getInventory() {
        return craftEvent.getInventory();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
