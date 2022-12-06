# MetaCraftingAPI
カスタムアイテムとレシピを追加するAPIプラグイン for Bukkit




## シンプルなサンプル例
### アイテムの作成
```java
String itemId = "sugoi_diamond";  // 他プラグインと被らない名前を
CustomItem sugoiDiamond = CustomItem.make(itemId, Material.DIAMOND, (item) -> {
  ItemMeta meta = item.getItemMeta();
  meta.setDisplayName("すごいダイヤ");
  item.setItemMeta(meta);
});
```
### レシピの作成
```java
NamespacedKey recipeKey = new NamespacedKey(yourPlugin, "customRecipeKey");  // 他のレシピと被らない名前を
CustomRecipe sugoiDiamondRecipe = CustomRecipe.builder(recipeKey)
  .shape("dd", "dd")  // Diaを2x2でクラフト
  .setIngredient('d', Material.DIAMOND)  // CustomItemも素材にセットできる
  .setResult(sugoiDia, 1)  // Materialも可。2番引数は個数
  .create();
```
※現在はShapedRecipeベースのみ対応
### アイテムとレシピの登録
```java
MetaCraftingAPI.registerCustomItem(yourPlugin, sugoiDiamond);
MetaCraftingAPI.registerCustomRecipe(yourPlugin, sugoiDiamondRecipe);
```
### 登録解除 (onDisableなどでアンロードしてほしい)
```java
MetaCraftingAPI.unregisterBy(yourPlugin);
```

## 前提
- Spigot 1.16 以上 (またはその派生)

