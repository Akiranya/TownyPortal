package co.mcsky.townyportal.gui;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModel;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.gui.base.PaginatedView;
import co.mcsky.townyportal.gui.base.SeamlessGui;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.paperlib.PaperLib;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PageInfo;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.StandardSchemeMappings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ShopListingView extends PaginatedView {

    private final static MenuScheme FILTER_BUY_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000100000");
    private final static MenuScheme FILTER_SELL_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000010000");
    private final static MenuScheme FILTER_ALL_SHOP = new MenuScheme()
            .maskEmpty(5)
            .mask("000001000");

    // the backed GUI
    private final SeamlessGui gui;
    // data source of shop models
    private final ShopModelDatasource shopModelDataSource;

    public ShopListingView(SeamlessGui gui, ShopModelDatasource shopModelDataSource) {
        super(gui);
        this.gui = gui;
        this.shopModelDataSource = shopModelDataSource;

        // update content upon this view creation
        updateContent(ShopFilters.ALL());
    }

    public void updateContent(Predicate<ShopModel> filter) {
        List<Item> content = shopModelDataSource.getShopList().stream().filter(filter).map(this::shopIcon).toList();
        updateContent(content);
    }

    private Item shopIcon(ShopModel s) {
        int shopQuantity = s.getQuantity();
        String buyPrice = s.hasBuyPrice()
                ? TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore2", "amount", shopQuantity, "buy_price", s.getBuyPrice())
                : TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore2-unavailable");
        String sellPrice = s.hasSellPrice()
                ? TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore3", "amount", shopQuantity, "sell_price", s.getSellPrice())
                : TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore3-unavailable");
        return ItemStackBuilder.of(s.getItem())
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.break-line"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore1", "owner", gui.getPlayer().getDisplayName()))
                .lore(buyPrice) // buy price
                .lore(sellPrice) // sell price
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.break-line"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore4")) // click to teleport
                .build(() -> {
                    try {
                        PaperLib.teleportAsync(gui.getPlayer(), s.getTown().getSpawn());
                    } catch (TownyException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void renderSubview() {
        FILTER_BUY_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.plugin.getMessage("gui.shop-listing.filter-buy-shop.name"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-buy-shop.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-buy-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.BUY());
                    render();
                }));
        FILTER_SELL_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.plugin.getMessage("gui.shop-listing.filter-sell-shop.name"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-sell-shop.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-sell-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.SELL());
                    render();
                }));
        FILTER_ALL_SHOP.newPopulator(gui).accept(ItemStackBuilder.of(Material.SPYGLASS)
                .name(TownyPortal.plugin.getMessage("gui.shop-listing.filter-all-shop.name"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-all-shop.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.filter-all-shop.lore2"))
                .build(() -> {
                    updateContent(ShopFilters.ALL());
                    render();
                }));
    }

    @Override
    public MenuScheme backgroundSchema() {
        return new MenuScheme(StandardSchemeMappings.STAINED_GLASS)
                .maskEmpty(5)
                .mask("111111111")
                .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15);
    }

    @Override
    public int nextPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("000000001")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public int previousPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("100000000")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public Function<PageInfo, ItemStack> nextPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.plugin.getMessage("gui.shop-listing.next-page.name"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.next-page.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.next-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public Function<PageInfo, ItemStack> previousPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.plugin.getMessage("gui.shop-listing.previous-page.name"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.previous-page.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.previous-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public List<Integer> itemSlots() {
        return new MenuScheme()
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .getMaskedIndexesImmutable();
    }
}
