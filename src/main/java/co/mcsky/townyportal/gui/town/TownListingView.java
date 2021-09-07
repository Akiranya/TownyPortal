package co.mcsky.townyportal.gui.town;

import co.mcsky.moecore.gui.PaginatedView;
import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.moecore.skull.SkinFetchCompleteEvent;
import co.mcsky.moecore.skull.SkullCache;
import co.mcsky.townyportal.TownyPortal;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PageInfo;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.StandardSchemeMappings;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 * This view is displayed when player runs "/towns".
 */
public class TownListingView extends PaginatedView {

    private static final MenuScheme POSTER = new MenuScheme().mask("000010000");
    private static final MenuScheme BACK = new MenuScheme().maskEmpty(5).mask("000000001");

    // the backed GUI
    private final SeamlessGui gui;
    // towny api instance
    private final TownyAPI towny;

    public TownListingView(SeamlessGui gui) {
        super(gui);

        // constructor args
        this.gui = gui;

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
        POSTER.newPopulator(this.gui).accept(ItemStackBuilder.of(Material.NETHER_STAR)
                .name(TownyPortal.text("gui.town-listing.menu-tips.name"))
                .lore(TownyPortal.text("gui.town-listing.menu-tips.lore1"))
                .lore(TownyPortal.text("gui.town-listing.menu-tips.lore2"))
                .lore(TownyPortal.text("gui.town-listing.menu-tips.lore3"))
                .lore(TownyPortal.text("gui.town-listing.menu-tips.lore4"))
                .buildItem().build());
        // place back button
        BACK.newPopulator(this.gui).accept(ItemStackBuilder.of(Material.REDSTONE)
                .name(TownyPortal.text("gui.back-icon.name"))
                .lore(TownyPortal.text("gui.back-icon.lore1"))
                .build(() -> this.gui.getPlayer().performCommand(TownyPortal.config().town_listing_back_command)));
    }

    @Override
    public MenuScheme backgroundSchema() {
        return new MenuScheme(StandardSchemeMappings.STAINED_GLASS)
                .mask("111111111")
                .mask("100000001")
                .mask("100000001")
                .mask("100000001")
                .mask("100000001")
                .mask("111111111")
                .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15)
                .scheme(15, 15)
                .scheme(15, 15)
                .scheme(15, 15)
                .scheme(15, 15)
                .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15);
    }

    @Override
    public int nextPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("100000000")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public int previousPageSlot() {
        return new MenuScheme()
                .maskEmpty(5)
                .mask("010000000")
                .getMaskedIndexesImmutable().get(0);
    }

    @Override
    public Function<PageInfo, ItemStack> nextPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.text("gui.town-listing.next-page.name"))
                .lore(TownyPortal.text("gui.town-listing.next-page.lore1"))
                .lore(TownyPortal.text("gui.town-listing.next-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public Function<PageInfo, ItemStack> previousPageItem() {
        return pageInfo -> ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.text("gui.town-listing.previous-page.name"))
                .lore(TownyPortal.text("gui.town-listing.previous-page.lore1"))
                .lore(TownyPortal.text("gui.town-listing.previous-page.lore2", "current_page", pageInfo.getCurrent(), "total_page", pageInfo.getSize()))
                .build();
    }

    @Override
    public List<Integer> itemSlots() {
        return new MenuScheme()
                .maskEmpty(1)
                .mask("011111110")
                .mask("011111110")
                .mask("011111110")
                .mask("011111110")
                .getMaskedIndexesImmutable();
    }

    public void updateContent() {
        List<Item> content = towny.getDataSource().getTowns().stream().map(this::townIcon).toList();
        updateContent(content);
    }

    @Contract("_ -> new")
    private @NotNull Item townIcon(@NotNull Town town) {
        return ItemStackBuilder.of(Material.PLAYER_HEAD)
                .name(TownyPortal.text("gui.town-listing.town-entry.name", "town_name", town.getName()))
                .lore("")
                .lore(TownyPortal.text("gui.town-listing.town-entry.lore1", "mayor_name", town.getMayor().getName()))
                .lore(TownyPortal.text("gui.town-listing.town-entry.lore2", "shop_num", TownyPortal.townModelDatasource().getTownModel(town).getShopNum()))
                .lore("")
                .lore(TownyPortal.townModelDatasource().getTownModel(town).getTownBoard())
                .lore("")
                .lore(TownyPortal.text("gui.town-listing.town-entry.lore3"))
                .transform(item -> SkullCache.INSTANCE.itemWithUuid(item, town.getMayor().getUUID()))
                .build(() -> {
                    Metadata.provideForPlayer(gui.getPlayer()).put(TownListingGui.CHOSEN_TOWN_KEY, town);
                    gui.switchView(new TownOptionView(gui, this));
                });
    }
}
