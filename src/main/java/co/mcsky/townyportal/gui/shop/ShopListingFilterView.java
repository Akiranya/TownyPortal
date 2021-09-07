package co.mcsky.townyportal.gui.shop;

import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.townyportal.TownyPortal;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

/**
 * This view is displayed when player runs "/ts shops".
 */
public class ShopListingFilterView extends ShopListingAbstractView {

    private final static MenuScheme FILTER = new MenuScheme()
            .maskEmpty(5)
            .mask("000111000");
    private final static MenuScheme BACK = new MenuScheme()
            .maskEmpty(5)
            .mask("000000001");

    public ShopListingFilterView(SeamlessGui gui, String shopType) {
        super(gui, shopType);
    }

    @Override
    public void renderSubview() {
        final MenuPopulator filterPopulator = FILTER.newPopulator(gui);
        filterPopulator.accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-buy-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-buy-shop.lore1"))
                .build(() -> {
                    updateContent(ShopFilters.BUY());
                    render();
                }));
        filterPopulator.accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-sell-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-sell-shop.lore1"))
                .build(() -> {
                    updateContent(ShopFilters.SELL());
                    render();
                }));
        filterPopulator.accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.text("gui.shop-listing.filter-all-shop.name"))
                .lore(TownyPortal.text("gui.shop-listing.filter-all-shop.lore1"))
                .build(() -> {
                    updateContent(ShopFilters.ALL());
                    render();
                }));
        BACK.newPopulator(this.gui).accept(ItemStackBuilder.of(Material.REDSTONE)
                .name(TownyPortal.text("gui.back-icon.name"))
                .lore(TownyPortal.text("gui.back-icon.lore1"))
                .build(() -> this.gui.getPlayer().performCommand(TownyPortal.config().shop_listing_back_command)));
    }
}
