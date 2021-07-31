package co.mcsky.townyportal.gui;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.TownModelDatasource;
import co.mcsky.townyportal.gui.base.PaginatedView;
import co.mcsky.townyportal.gui.base.SeamlessGui;
import co.mcsky.townyportal.skull.SkinFetchCompleteEvent;
import co.mcsky.townyportal.skull.SkullCache;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PageInfo;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.StandardSchemeMappings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class TownListingView extends PaginatedView {

    // the backed GUI
    private final SeamlessGui gui;
    private final MenuScheme POSTER = new MenuScheme().mask("000010000");

    // towny api instance
    private final TownyAPI towny;
    // data source
    private final TownModelDatasource townModelDataSource;
    // currently viewing town
    private Town chosenTown;

    public TownListingView(SeamlessGui gui, TownModelDatasource townModelDataSource) {
        super(gui);

        // constructor args
        this.gui = gui;
        this.townModelDataSource = townModelDataSource;

        // convenient field
        this.towny = TownyAPI.getInstance();

        // update content when created
        this.updateContent();

        // refresh the gui whenever a skin is fetched
        Events.subscribe(SkinFetchCompleteEvent.class)
                .handler(e -> gui.redraw())
                .bindWith(gui);
    }

    @Override
    public void renderSubview() {
        // place the poster
        this.POSTER.newPopulator(this.gui).accept(ItemStackBuilder.of(Material.NETHER_STAR)
                .name(TownyPortal.plugin.getMessage("gui.town-listing.menu-tips.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.menu-tips.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.menu-tips.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.menu-tips.lore3"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.menu-tips.lore4"))
                .buildItem().build());
    }

    @Override
    public MenuScheme backgroundSchema() {
        return new MenuScheme(StandardSchemeMappings.STAINED_GLASS)
                .mask("111111111")
                .mask("110000011")
                .mask("110000011")
                .mask("110000011")
                .mask("110000011")
                .mask("111111111")
                .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15)
                .scheme(15, 15, 15, 15)
                .scheme(15, 15, 15, 15)
                .scheme(15, 15, 15, 15)
                .scheme(15, 15, 15, 15)
                .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15);
    }

    @Override
    public int nextPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("000000100")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public int previousPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("001000000")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public Function<PageInfo, ItemStack> nextPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.plugin.getMessage("gui.town-listing.next-page.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.next-page.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.next-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public Function<PageInfo, ItemStack> previousPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.plugin.getMessage("gui.town-listing.previous-page.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.previous-page.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.previous-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public List<Integer> itemSlots() {
        return new MenuScheme()
                .maskEmpty(1)
                .mask("001111100")
                .mask("001111100")
                .mask("001111100")
                .mask("001111100")
                .getMaskedIndexesImmutable();
    }

    public void updateContent() {
        List<Item> content = towny.getDataSource().getTowns().stream().map(this::townIcon).toList();
        updateContent(content);
    }

    public Town getChosenTown() {
        return chosenTown;
    }

    private Item townIcon(Town town) {
        return ItemStackBuilder.of(Material.PLAYER_HEAD)
                .name(TownyPortal.plugin.getMessage("gui.town-listing.town-entry.name", "town_name", town.getName()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.town-entry.lore1", "mayor_name", town.getMayor().getName()))
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.town-entry.lore2", "shop_num", townModelDataSource.getTownModel(town.getUUID()).getShopNum()))
                .lore("")
                .lore(townModelDataSource.getTownModel(town.getUUID()).getTownBoard())
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-listing.town-entry.lore3"))
                .transform(item -> SkullCache.INSTANCE.itemWithUuid(item, town.getMayor().getUUID()))
                .build(() -> {
                    this.chosenTown = town;
                    this.gui.switchView(new TownOptionView(this.gui, this, this.townModelDataSource));
                });
    }
}
