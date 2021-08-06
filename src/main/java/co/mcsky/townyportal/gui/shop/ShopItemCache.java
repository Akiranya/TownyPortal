package co.mcsky.townyportal.gui.shop;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModel;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.lucko.helper.item.ItemStackBuilder;

import java.util.concurrent.TimeUnit;

public class ShopItemCache {

    private static final LoadingCache<ShopModel, ItemStackBuilder> shopIconCache = CacheBuilder.newBuilder()
            .expireAfterAccess(TownyPortal.plugin.config.shop_icon_cache_timeout, TimeUnit.SECONDS)
            .build(CacheLoader.from(model -> {
                int shopQuantity = model.getQuantity();
                String buyPrice = model.hasBuyPrice()
                        ? TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore3", "amount", shopQuantity, "buy_price", model.getBuyPrice())
                        : TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore3-unavailable");
                String sellPrice = model.hasSellPrice()
                        ? TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore4", "amount", shopQuantity, "sell_price", model.getSellPrice())
                        : TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore4-unavailable");
                return ItemStackBuilder.of(model.getItem())
                        .lore(TownyPortal.plugin.message("gui.shop-listing.shop-icon.break-line"))
                        .lore(TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore1", "owner", model.ownerName()))
                        .lore(TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore2", "town", model.getTown().getName()))
                        .lore(buyPrice) // buy price
                        .lore(sellPrice) // sell price
                        .lore(TownyPortal.plugin.message("gui.shop-listing.shop-icon.break-line"))
                        .lore(TownyPortal.plugin.message("gui.shop-listing.shop-icon.lore5"));
            }));

    public static ItemStackBuilder get(ShopModel model) {
        return shopIconCache.getUnchecked(model);
    }

    public static void refresh(ShopModel model) {
        shopIconCache.refresh(model);
    }

}
