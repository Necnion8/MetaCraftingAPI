package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaCraftPrepareSmithEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    private final PrepareSmithingEvent baseEvent;
    private Result action = Result.DEFAULT;
    private final SlotItem[] slots;
    private @Nullable ItemStack result;

    public MetaCraftPrepareSmithEvent(PrepareSmithingEvent baseEvent, SlotItem[] slots, @Nullable ItemStack result) {
        this.baseEvent = baseEvent;
        this.slots = slots;
        this.result = result;
    }

    public PrepareSmithingEvent getBaseEvent() {
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

    public SmithingInventory getInventory() {
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
