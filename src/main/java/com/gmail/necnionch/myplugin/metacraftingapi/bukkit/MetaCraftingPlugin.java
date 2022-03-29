package com.gmail.necnionch.myplugin.metacraftingapi.bukkit;

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
