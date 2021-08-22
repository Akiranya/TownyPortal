package co.mcsky.townyportal.gui.shop;

import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.townyportal.TownyPortal;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

public class ShopListingFilterView extends ShopListingAbstractView {

    private final static MenuScheme FILTER_BUY_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000100000");
    private final static MenuScheme FILTER_SELL_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000010000");
    private final static MenuScheme FILTER_ALL_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000001000");

    public ShopListingFilterView(SeamlessGui gui, String shopType) {
        super(gui, shopType);
    }

    @Override
    public void renderSubview() {
        FILTER_BUY_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-buy-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-buy-shop.lore1"))
                .lore(TownyPortal.text("gui.shop-listing.filter-buy-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.BUY());
                    render();
                }));
        FILTER_SELL_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-sell-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-sell-shop.lore1"))
                .lore(TownyPortal.text("gui.shop-listing.filter-sell-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.SELL());
                    render();
                }));
        FILTER_ALL_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-all-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-all-shop.lore1"))
                .lore(TownyPortal.text("gui.shop-listing.filter-all-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.ALL());
                    render();
                }));
    }
}
