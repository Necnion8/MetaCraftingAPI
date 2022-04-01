package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.MetaCraftingPlugin;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DummyShapedRecipe extends ShapedRecipe {
    private final Set<CustomRecipe> recipes = Sets.newHashSet();

    public DummyShapedRecipe(NamespacedKey key) {
        super(key, createDummyItem());
    }

    public static DummyShapedRecipe fromRecipe(CustomRecipe recipe) {
        String key = Arrays.stream(recipe.getShapedIngredients())
                .map(rows -> Arrays.stream(rows)
                        .map(i -> i.getRecipeMaterial().getKey().getKey())
                        .collect(Collectors.joining("-")))
                .collect(Collectors.joining("_"));

        DummyShapedRecipe dummy = new DummyShapedRecipe(new NamespacedKey(MetaCraftingPlugin.getInstance(), "dummy_" + key));

        dummy.shape(recipe.getShape());
        recipe.ingredients().forEach((c, i) -> dummy.setIngredient(c, i.getRecipeMaterial()));

        return dummy;
    }

    @Deprecated
    public static DummyShapedRecipe fromRecipe(CustomRecipe recipe, NamespacedKey recipeKey) {
        DummyShapedRecipe dummy = new DummyShapedRecipe(recipeKey);

        dummy.shape(recipe.getShape());
        recipe.ingredients().forEach((c, i) -> dummy.setIngredient(c, i.getRecipeMaterial()));

        return dummy;
    }


    public Set<CustomRecipe> recipes() {
        return recipes;
    }

    public boolean equalsIngredient(CustomRecipe recipe) {
        Material[][] targetIngredients = Arrays.stream(recipe.getShapedIngredients())
                .map(row -> Arrays.stream(row).map(RecipeIngredient::getRecipeMaterial).toArray(Material[]::new))
                .toArray(Material[][]::new);

        Map<Character, ItemStack> thisCharsIngredient = getIngredientMap();
        Material[][] thisIngredients = Arrays.stream(getShape())
                .map(CharSequence::chars)
                .map(chars -> chars.mapToObj(c -> thisCharsIngredient.get((char) c))
                        .map(i -> (i != null) ? i.getType() : null)
                        .toArray(Material[]::new))
                .toArray(Material[][]::new);

        return Arrays.deepEquals(targetIngredients, thisIngredients);
    }


    public static ItemStack createDummyItem() {
        ItemStack dummy = new ItemStack(Material.STONE);
        ItemMeta meta = dummy.getItemMeta();
        meta.setDisplayName("§7RECIPE_DUMMY");
        meta.getPersistentDataContainer().set(new NamespacedKey(MetaCraftingPlugin.getInstance(), "dummy"), PersistentDataType.INTEGER, 1);
        dummy.setItemMeta(meta);
        dummy.setAmount(1);
        return dummy;
    }

    public static boolean isDummyItem(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            return meta.getPersistentDataContainer().has(new NamespacedKey(MetaCraftingPlugin.getInstance(), "dummy"), PersistentDataType.INTEGER);
        }
        return false;
    }

}
