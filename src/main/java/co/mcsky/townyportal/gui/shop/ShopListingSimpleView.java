package co.mcsky.townyportal.gui.shop;

import co.mcsky.moecore.gui.PaginatedView;
import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModel;
import co.mcsky.townyportal.data.ShopModelDatasource;
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

public abstract class ShopListingSimpleView extends PaginatedView {

    // the backed GUI
    protected final SeamlessGui gui;
    // data source of shop models
    protected final ShopModelDatasource shopModelDataSource;

    public ShopListingSimpleView(SeamlessGui gui, ShopModelDatasource shopModelDataSource) {
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
                ? TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore3", "amount", shopQuantity, "buy_price", s.getBuyPrice())
                : TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore3-unavailable");
        String sellPrice = s.hasSellPrice()
                ? TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore4", "amount", shopQuantity, "sell_price", s.getSellPrice())
                : TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore4-unavailable");
        return ItemStackBuilder.of(s.getItem())
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.break-line"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore1", "owner", s.ownerName()))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore2", "town", s.getTown().getName()))
                .lore(buyPrice) // buy price
                .lore(sellPrice) // sell price
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.break-line"))
                .lore(TownyPortal.plugin.getMessage("gui.shop-listing.shop-icon.lore5")) // click to teleport
                .build(() -> {
                    try {
                        PaperLib.teleportAsync(gui.getPlayer(), s.getTown().getSpawn());
                    } catch (TownyException e) {
                        e.printStackTrace();
                    }
                });
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
