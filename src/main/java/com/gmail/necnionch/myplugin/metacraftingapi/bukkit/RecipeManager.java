package com.gmail.necnionch.myplugin.metacraftingapi.bukkit;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.events.MetaCraftPrepareItemCraftEvent;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.CustomItem;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.CustomRecipe;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.DummyShapedRecipe;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe.RecipeIngredient;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;


public class RecipeManager extends MetaCraftingAPI implements Listener {
    public static final List<Function<CraftingInventory, Integer>> MATRIX_SIZE_LOOKUPS;
    private final MetaCraftingPlugin plugin;
    private final Map<String, CustomItem> itemById = Maps.newHashMap();
    private final Multimap<Plugin, CustomItem> itemsByPlugin = ArrayListMultimap.create();
    private final Map<NamespacedKey, CustomRecipe> recipeByKey = Maps.newHashMap();
    private final Multimap<Plugin, CustomRecipe> recipesByPlugin = ArrayListMultimap.create();
    private final Map<NamespacedKey, DummyShapedRecipe> dummyShapedRecipes = Maps.newHashMap();

    static {
        MATRIX_SIZE_LOOKUPS = Lists.newArrayList();
        MATRIX_SIZE_LOOKUPS.add(inv -> InventoryType.WORKBENCH.equals(inv.getType()) ? 3 : null);
        MATRIX_SIZE_LOOKUPS.add(inv -> InventoryType.CRAFTING.equals(inv.getType()) ? 2 : null);
    }

    public RecipeManager(MetaCraftingPlugin plugin) {
        this.plugin = plugin;
    }


    // listeners

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        NamespacedKey recipeKey = (event.getRecipe() instanceof Keyed) ? ((Keyed) event.getRecipe()).getKey() : null;
        CraftingInventory crafting = event.getInventory();

        Event.Result action;
        CustomRecipe customRecipe = null;
        boolean dummyRecipe = false;
        ItemStack result = null;

        // レシピを提供するダミーレシピだった場合
        if (recipeKey != null && dummyShapedRecipes.containsKey(recipeKey)) {
            dummyRecipe = true;
            action = Event.Result.DENY;

            try {
                int matrixSize = MATRIX_SIZE_LOOKUPS.stream()
                        .map(value -> value.apply(crafting))
                        .filter(Objects::nonNull)
                        .findFirst().orElse(-1);

                if (matrixSize >= 1) {
                    DummyShapedRecipe dummy = dummyShapedRecipes.get(recipeKey);
                    ItemStack[][] resized = resizeMatrix(crafting.getMatrix(), matrixSize, matrixSize);
                    customRecipe = findMatchIngredientRecipe(resized, dummy);

                    if (customRecipe != null) {
                        result = customRecipe.getResultItem().createItemStack();
                        result.setAmount(customRecipe.getResultAmount());

                        action = Event.Result.ALLOW;
//                        plugin.getLogger().info("set by " + customRecipe.getKey());

//                    } else {
//                        plugin.getLogger().info("unset");
                    }
                }

            } catch (Throwable e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to processing recipe", e);
                result = null;
            }

        } else {
            // others
            action = Event.Result.ALLOW;

            for (ItemStack item : crafting.getMatrix()) {
                if (CustomItem.hasItemId(item)) {
                    action = Event.Result.DENY;
//                    plugin.getLogger().info("set result to null");
                    break;
                }
            }
        }

        MetaCraftPrepareItemCraftEvent metaCraftEvent = new MetaCraftPrepareItemCraftEvent(event, result, action, dummyRecipe, customRecipe);
        plugin.getServer().getPluginManager().callEvent(metaCraftEvent);

        if (DummyShapedRecipe.isDummyItem(crafting.getResult()))
            crafting.setResult(null);

        if (metaCraftEvent.isCancelled())
            return;  // ignored

        if (Event.Result.DENY.equals(metaCraftEvent.getAction())) {
            crafting.setResult(null);
        } else if (metaCraftEvent.isDummyRecipe()) {
            crafting.setResult(metaCraftEvent.getResult());
        }

    }


    // methods

    private void registerRecipe(CustomRecipe recipe) {
        DummyShapedRecipe dummyRecipe = findMatchIngredientDummyRecipe(recipe);
        if (dummyRecipe == null) {
            dummyRecipe = DummyShapedRecipe.fromRecipe(recipe);
            Bukkit.addRecipe(dummyRecipe);
            dummyShapedRecipes.put(dummyRecipe.getKey(), dummyRecipe);
        }

        dummyRecipe.recipes().add(recipe);
    }

    private void unregisterRecipe(CustomRecipe recipe) {
        for (Iterator<DummyShapedRecipe> it = dummyShapedRecipes.values().iterator(); it.hasNext(); ) {
            DummyShapedRecipe dummy = it.next();
            if (dummy.recipes().remove(recipe)) {
                it.remove();
                unregisterBukkitRecipe(dummy);
            }
        }
    }

    private void unregisterBukkitRecipe(ShapedRecipe recipe) {
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe entry = it.next();
            if (entry instanceof Keyed && ((Keyed) entry).getKey().equals(recipe.getKey()))
                it.remove();
        }
    }

    private DummyShapedRecipe findMatchIngredientDummyRecipe(CustomRecipe recipe) {
        return dummyShapedRecipes.values().stream()
                .filter(d -> d.equalsIngredient(recipe))
                .findFirst().orElse(null);
    }

    public static ItemStack[][] resizeMatrix(ItemStack[] matrix, int inRow, int inCol) {
        // 0, 1, 2
        // 3, 4, 5
        // 6, 7, 8

        // find min pos
        int minRow = inRow - 1, minCol = inCol - 1;
        int maxRow = 0, maxCol = 0;
        for (int idx = 0; idx < (inRow * inCol); idx++) {
            int row = idx % inRow;
            int col = idx / inCol;

            if (matrix[idx] != null && !Material.AIR.equals(matrix[idx].getType())) {
                minRow = Math.min(row, minRow);
                minCol = Math.min(col, minCol);
                maxRow = Math.max(row, maxRow);
                maxCol = Math.max(col, maxCol);
            }
//            getLogger().info("index " + idx + " " + matrix[idx]);
        }

        int rowSize = maxRow - minRow;
        int colSize = maxCol - minCol;

        // compress matrix
        ItemStack[][] newMatrix = new ItemStack[colSize+1][rowSize+1];

        for (int idx = 0; idx < (inRow * inCol); idx++) {
            int row = idx % inRow;
            int col = idx / inCol;

            if (minRow <= row && row <= maxRow)
                if (minCol <= col && col <= maxCol) {
                    newMatrix[col - minCol][row - minRow] = matrix[idx];
                }
        }
        return newMatrix;
    }

    private CustomRecipe findMatchIngredientRecipe(ItemStack[][] input, DummyShapedRecipe dummyRecipe) {
        // input
        int inputItems = Arrays.stream(input).mapToInt(row -> row.length).sum();

        // match test
        int outputItems;
        for (CustomRecipe recipe : dummyRecipe.recipes()) {
            // check
            RecipeIngredient[][] output = recipe.getShapedIngredients();
            outputItems = Arrays.stream(output).mapToInt(row -> row.length).sum();
            if (inputItems != outputItems)
                continue;  // program error

            if (matchIngredient(input, output))
                return recipe;
        }
        return null;
    }

    private boolean matchIngredient(ItemStack[][] input, RecipeIngredient[][] output) {
        int pattern = -1;  // 0 = normalOrFlipped, 1 = normal, 2 = flipped

        for (int col = 0; col < input.length; col++) {
            ItemStack[] cols = input[col];
            for (int row = 0; row < cols.length; row++) {
                ItemStack in = cols[row];
                RecipeIngredient out = output[col][row];

                // normal pattern
                boolean noMatch = (in == null) == (out != null);
                if (!noMatch && out != null && !out.equalsItemType(in))
                    noMatch = true;

                // flip pattern
                out = output[col][cols.length - row - 1];
                boolean noMatchFlip = (in == null) == (out != null);
                if (!noMatchFlip && out != null && !out.equalsItemType(in))
                    noMatchFlip = true;

                // matches check
                if (noMatch && noMatchFlip) {
                    return false;
                } else if (!noMatch && !noMatchFlip) {
                    if (pattern == -1)
                        pattern = 0;
                } else if (noMatch) {
                    if (pattern == 1)
                        return false;
                    pattern = 2;
                } else {
                    if (pattern == 2)
                        return false;
                    pattern = 1;
                }
            }
        }
        return true;

    }

    public void unloadAll() {
        itemById.clear();
        itemsByPlugin.clear();
        recipeByKey.clear();
        recipesByPlugin.clear();
        dummyShapedRecipes.values().forEach(this::unregisterBukkitRecipe);
        dummyShapedRecipes.clear();
    }

    // api

    @Override
    protected void registerCustomItemImpl(Plugin owner, CustomItem customItem) {
        String itemId = customItem.getItemId().toLowerCase(Locale.ROOT);

        if (itemById.containsKey(itemId))
            throw new IllegalArgumentException("already registered item id : " + itemId);

        itemById.put(itemId, customItem);
        itemsByPlugin.put(owner, customItem);
    }

    @Override
    protected void unregisterCustomItemImpl(CustomItem customItem) {
        if (itemById.containsValue(customItem))
            itemById.entrySet().removeIf(e -> e.getValue().equals(customItem));
        if (itemsByPlugin.containsValue(customItem))
            itemsByPlugin.values().remove(customItem);
    }

    @Override
    protected void unregisterAllCustomItemsImpl(Plugin owner) {
        Collection<CustomItem> items = itemsByPlugin.removeAll(owner);
        itemById.values().removeAll(items);
    }

    @Override
    protected Collection<CustomItem> getCustomItemsImpl(@Nullable Plugin owner) {
        if (owner != null)
            return Collections.unmodifiableCollection(itemsByPlugin.get(owner));

        return Collections.unmodifiableCollection(itemById.values());
    }

    @Override
    protected @Nullable CustomItem getCustomItemByItemStackImpl(ItemStack itemStack) {
        String itemId = CustomItem.getItemId(itemStack);
        if (itemId != null)
            return itemById.get(itemId);
        return null;
    }

    @Override
    protected @Nullable CustomItem getCustomItemImpl(String itemId) {
        return itemById.get(itemId);
    }

    @Override
    protected void registerCustomRecipeImpl(Plugin owner, CustomRecipe customRecipe) {
        NamespacedKey recipeKey = customRecipe.getKey();

        if (recipeByKey.containsKey(recipeKey))
            throw new IllegalArgumentException("already registered recipe key : " + recipeKey);

        registerRecipe(customRecipe);

        recipeByKey.put(recipeKey, customRecipe);
        recipesByPlugin.put(owner, customRecipe);
    }

    @Override
    protected void unregisterCustomRecipeImpl(CustomRecipe customRecipe) {
        boolean removed = false;
        if (recipeByKey.containsValue(customRecipe))
            removed = recipeByKey.entrySet().removeIf(e -> e.getValue().equals(customRecipe));
        if (recipesByPlugin.containsValue(customRecipe)) {
            if (recipesByPlugin.values().remove(customRecipe))
                removed = true;
        }

        if (removed)
            unregisterRecipe(customRecipe);
    }

    @Override
    protected void unregisterAllCustomRecipesImpl(Plugin owner) {
        Collection<CustomRecipe> recipes = recipesByPlugin.removeAll(owner);
        recipeByKey.values().removeAll(recipes);
        recipes.forEach(this::unregisterRecipe);
    }

    @Override
    protected Collection<CustomRecipe> getCustomRecipesImpl(@Nullable Plugin owner) {
        if (owner != null)
            return Collections.unmodifiableCollection(recipesByPlugin.get(owner));
        return Collections.unmodifiableCollection(recipesByPlugin.values());
    }

    @Override
    protected @Nullable CustomRecipe getCustomRecipeImpl(NamespacedKey recipeKey) {
        return recipeByKey.get(recipeKey);
    }

}
