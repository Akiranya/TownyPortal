package co.mcsky.townyportal.gui.shop;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.item.ItemStackBuilder;

public class ShopItemCache {

    private static final LoadingCache<ShopModel, ItemStackBuilder> shopIconCache;

    static {
        shopIconCache = CacheBuilder.newBuilder().build(CacheLoader.from(model -> {
            final Town town = model.getTown();
            String townName;
            if (town != null) {
                townName = town.getName();
            } else {
                townName = "NULL";
            }
            return ItemStackBuilder.of(model.getItem())
                    .lore(TownyPortal.text("gui.shop-listing.shop-icon.break-line"))
                    .lore(TownyPortal.text("gui.shop-listing.shop-icon.lore1", "owner", model.ownerName()))
                    .lore(TownyPortal.text("gui.shop-listing.shop-icon.lore2", "town", townName))
                    .lore(model.hasBuyPrice()
                            ? TownyPortal.text("gui.shop-listing.shop-icon.lore3", "amount", model.getQuantity(), "buy_price", model.getBuyPrice())
                            : TownyPortal.text("gui.shop-listing.shop-icon.lore3-unavailable"))
                    .lore(model.hasSellPrice()
                            ? TownyPortal.text("gui.shop-listing.shop-icon.lore4", "amount", model.getQuantity(), "sell_price", model.getSellPrice())
                            : TownyPortal.text("gui.shop-listing.shop-icon.lore4-unavailable"))
                    .lore(TownyPortal.text("gui.shop-listing.shop-icon.break-line"))
                    .lore(TownyPortal.text("gui.shop-listing.shop-icon.lore5"));
        }));
    }

    public static ItemStackBuilder get(ShopModel model) {
        return shopIconCache.getUnchecked(model);
    }

    public static void refresh(ShopModel model) {
        shopIconCache.refresh(model);
    }

    public static void clear() {
        shopIconCache.invalidateAll();
    }

}
