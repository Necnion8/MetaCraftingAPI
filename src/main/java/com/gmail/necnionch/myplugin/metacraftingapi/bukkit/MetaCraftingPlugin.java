package com.gmail.necnionch.myplugin.metacraftingapi.bukkit;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.listeners.AnvilListener;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.listeners.SmithingListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;


public final class MetaCraftingPlugin extends JavaPlugin {
    private static MetaCraftingPlugin instance;
    private final RecipeManager api = new RecipeManager(this);

    @Override
    public void onEnable() {
        instance = this;

        try {
            Field field = MetaCraftingAPI.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, api);
            field.setAccessible(false);
        } catch (ReflectiveOperationException e) {
            getLogger().severe("Failed to initialize API");
        }

        getServer().getPluginManager().registerEvents(api, this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);

        try {
            Class.forName("org.bukkit.event.inventory.PrepareSmithingEvent");
            getServer().getPluginManager().registerEvents(new SmithingListener(this), this);
        } catch (ClassNotFoundException ignored) {
            // 1.15 and older
        }

    }

    @Override
    public void onDisable() {
        try {
            Field field = MetaCraftingAPI.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
            field.setAccessible(false);
        } catch (ReflectiveOperationException ignored) {}

        api.unloadAll();
    }

    public static MetaCraftingPlugin getInstance() {
        return instance;
    }

}
