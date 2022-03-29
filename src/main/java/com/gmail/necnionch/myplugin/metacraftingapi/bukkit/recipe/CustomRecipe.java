package com.gmail.necnionch.myplugin.metacraftingapi.bukkit.recipe;

import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.CreatableItemStack;
import com.gmail.necnionch.myplugin.metacraftingapi.bukkit.item.CustomItem;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;


public class CustomRecipe {
    private final String[] shape;
    private final Map<Character, RecipeIngredient> ingredients;
    private final int resultAmount;
    private final CreatableItemStack resultItem;
    private final NamespacedKey recipeKey;
    private final RecipeIngredient[][] shapedIngredients;

    public CustomRecipe(NamespacedKey recipeKey, Builder builder) {
        this.recipeKey = recipeKey;
        shape = builder.shape;
        ingredients = builder.ingredients;
        resultAmount = builder.resultAmount;
        resultItem = builder.resultItem;

        shapedIngredients = new RecipeIngredient[shape.length][];
        for (int row = 0; row < shape.length; row++) {
            String rowKeys = shape[row];
            shapedIngredients[row] = rowKeys.chars()
                    .mapToObj(k -> ingredients.getOrDefault((char) k, null))
                    .toArray(RecipeIngredient[]::new);
        }
    }


    public static Builder builder(NamespacedKey recipeKey) {
        return new Builder(recipeKey);
    }


    public NamespacedKey getKey() {
        return recipeKey;
    }

    public String[] getShape() {
        return shape;
    }

    public Map<Character, RecipeIngredient> ingredients() {
        return Collections.unmodifiableMap(ingredients);
    }

    public RecipeIngredient[][] getShapedIngredients() {
        return shapedIngredients;
    }

    public int getResultAmount() {
        return resultAmount;
    }

    public CreatableItemStack getResultItem() {
        return resultItem;
    }



    public final static class Builder {
        private final NamespacedKey recipeKey;
        private String[] shape;
        private final Map<Character, RecipeIngredient> ingredients = Maps.newHashMap();
        private CreatableItemStack resultItem;
        private int resultAmount = 1;

        public Builder(NamespacedKey recipeKey) {
            this.recipeKey = recipeKey;
        }

        public CustomRecipe create() {
            if (resultItem == null)
                throw new IllegalArgumentException("result item is not set!");

            if (shape == null)
                throw new IllegalArgumentException("shape is not set!");

            try {
                ShapedRecipe test = new ShapedRecipe(NamespacedKey.minecraft("test"), DummyShapedRecipe.createDummyItem());
                test.shape(shape);
                ingredients.forEach((c, i) -> test.setIngredient(c, i.getRecipeMaterial()));
            } catch (Throwable e) {
                throw new IllegalArgumentException("Invalid recipe setup : " + recipeKey, e);
            }

            return new CustomRecipe(recipeKey, this);
        }

        public Builder clone() {
            Builder b = new Builder(recipeKey);
            b.shape = shape;
            b.ingredients.putAll(ingredients);
            b.resultItem = resultItem;
            b.resultAmount = resultAmount;
            return b;
        }


        public Builder shape(final String... shapes) {
            shape = shapes;
            return this;
        }

        public Builder setIngredient(char key, RecipeIngredient ingredient) {
            ingredients.put(key, ingredient);
            return this;
        }

        public Builder setIngredient(char key, Material material) {
            ingredients.put(key, new RecipeIngredient() {
                public @NotNull Material getRecipeMaterial() { return material; }
                public boolean equalsItemType(@Nullable ItemStack itemStack) {
                    if (itemStack == null || !material.equals(itemStack.getType()))
                        return false;
                    return !CustomItem.hasItemId(itemStack);
                }
            });
            return this;
        }

        public Builder setResult(CreatableItemStack item, int amount) {
            resultAmount = amount;
            resultItem = item;
            return this;
        }

        public Builder setResult(Material material, int amount) {
            resultAmount = amount;
            resultItem = () -> new ItemStack(material);
            return this;
        }


    }

}
