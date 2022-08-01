package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaCraftPrepareAnvilEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final PrepareAnvilEvent baseEvent;
    private Result action = Result.DEFAULT;
    private final SlotItem[] slots;
    private @Nullable ItemStack result;

    public MetaCraftPrepareAnvilEvent(PrepareAnvilEvent baseEvent, SlotItem[] slots, @Nullable ItemStack result) {
        this.baseEvent = baseEvent;
        this.slots = slots;
        this.result = result;
    }

    public PrepareAnvilEvent getBaseEvent() {
        return baseEvent;
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

    public SlotItem[] getSlots() {
        return slots;
    }

    public AnvilInventory getInventory() {
        return baseEvent.getInventory();
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
