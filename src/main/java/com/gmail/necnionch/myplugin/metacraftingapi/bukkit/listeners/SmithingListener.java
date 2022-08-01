package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.listeners;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.MetaCraftingAPI;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.MetaCraftingPlugin;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events.MetaCraftPrepareSmithEvent;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events.SlotItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.SmithingInventory;

public class SmithingListener implements Listener {
    private final MetaCraftingPlugin plugin;

    public SmithingListener(MetaCraftingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepare(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        SlotItem[] slots = new SlotItem[] {
                new SlotItem(inv.getItem(0), MetaCraftingAPI.getCustomItemByItemStack(inv.getItem(0))),
                new SlotItem(inv.getItem(1), MetaCraftingAPI.getCustomItemByItemStack(inv.getItem(1)))
        };

        if (slots[0].getCustomItem() == null && slots[1].getCustomItem() == null)
            return;

        MetaCraftPrepareSmithEvent newEvent = new MetaCraftPrepareSmithEvent(event, slots, event.getResult());
        plugin.getServer().getPluginManager().callEvent(newEvent);

        if (Event.Result.ALLOW.equals(newEvent.getAction())) {
            event.setResult(newEvent.getResult());
        } else {
            event.setResult(null);
        }

    }

}
