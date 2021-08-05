package co.mcsky.townyportal.gui.town;

import co.mcsky.moecore.gui.GuiView;
import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.moecore.skull.SkullCreator;
import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.gui.shop.ShopListingTownView;
import com.google.common.collect.Lists;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.paperlib.PaperLib;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.StandardSchemeMappings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class TownOptionView implements GuiView {

    public static final String skin = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkNzBhYTMzNWVlYTNlNDY2Zjk2N2MyM2JhNTFlYjgyNzY1YTkwNzIwYzkwOTZiNzBmOGIxYzY1ZmMyYzc3MCJ9fX0=";

    private final SeamlessGui gui;

    private final MenuScheme BACKGROUND = new MenuScheme(StandardSchemeMappings.STAINED_GLASS)
            .mask("111111111")
            .mask("101111111")
            .mask("101000001")
            .mask("101000001")
            .mask("111111111")
            .mask("111111111")
            .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15)
            .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15)
            .scheme(15, 15, 15, 15, 15, 15)
            .scheme(15, 15, 15, 15, 15, 15)
            .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15)
            .scheme(15, 15, 15, 15, 15, 15, 15, 15, 15);
    private final MenuScheme LEFT_OPTIONS = new MenuScheme()
            .maskEmpty(1)
            .mask("010000000")
            .mask("010000000")
            .mask("010000000");
    private final MenuScheme RIGHT_OPTIONS = new MenuScheme()
            .maskEmpty(2)
            .mask("000111110")
            .mask("000111110");
    private final MenuScheme BACK_BUTTON = new MenuScheme()
            .maskEmpty(5)
            .mask("000010000");

    // the parent view
    private final GuiView parentView;
    // shop model data source
    private final ShopModelDatasource shopModelDatasource;

    // convenient fields
    private final TownyAPI townyApi;
    private final Town chosenTown;

    public TownOptionView(SeamlessGui gui,
                          TownListingView townListingView,
                          ShopModelDatasource shopModelDatasource) {
        this.gui = gui;
        this.parentView = townListingView;
        this.shopModelDatasource = shopModelDatasource;

        // convenient fields
        this.townyApi = TownyAPI.getInstance();
        this.chosenTown = townListingView.getChosenTown();
    }

    @Override
    public void render() {
        Player player = this.gui.getPlayer();

        // place background
        this.BACKGROUND.apply(this.gui);

        MenuPopulator leftOptionPopulator = this.LEFT_OPTIONS.newPopulator(this.gui);

        // check out shops
        leftOptionPopulator.accept(ItemStackBuilder.of(Material.GOLD_INGOT)
                .name(TownyPortal.plugin.getMessage("gui.town-options.shops.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.shops.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.shops.lore2"))
                .build(() -> gui.switchView(new ShopListingTownView(this, gui, shopModelDatasource, chosenTown.getUUID()))));
        // teleport to the town
        leftOptionPopulator.accept(ItemStackBuilder.of(Material.MINECART)
                .name(TownyPortal.plugin.getMessage("gui.town-options.teleport-to-town.name", "town_name", chosenTown.getName()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.teleport-to-town.lore1"))
                .build(() -> {
                    try {
                        PaperLib.teleportAsync(player, chosenTown.getSpawn());
                    } catch (TownyException e) {
                        player.sendMessage(e.getMessage());
                    }
                }));
        // join the town
        leftOptionPopulator.accept(ItemStackBuilder.of(Material.PAPER)
                .name(TownyPortal.plugin.getMessage("gui.town-options.join-town.name", "town_name", chosenTown.getName()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.join-town.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.join-town.lore2"))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.join-town.lore3"))
                .build(() -> {
                    Resident resident = townyApi.getResident(player.getUniqueId());
                    try {
                        chosenTown.addResidentCheck(resident);
                        if (chosenTown.isOpen() && resident != null) {
                            resident.setTown(chosenTown);
                        } else {
                            player.chat(TownyPortal.plugin.getMessage("chat-message.say-to-join-town"));
                        }
                    } catch (AlreadyRegisteredException e) {
                        player.sendMessage(e.getMessage());
                    }
                }));

        MenuPopulator rightOptionPopulator = this.RIGHT_OPTIONS.newPopulator(this.gui);

        // place right option: town level
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.EMERALD)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-level.name", "prefix", chosenTown.getPrefix(), "postfix", chosenTown.getPostfix()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-level.lore1"))
                .buildItem().build());

        // place right option: town resident list
        List<String> strings = chosenTown.getResidents().stream().map(TownyObject::getName).toList();
        List<List<String>> partitions = Lists.partition(strings, TownyPortal.plugin.config.resident_name_num_per_line);
        ItemStackBuilder item = ItemStackBuilder.of(Material.PLAYER_HEAD)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-resident.name", "resident_num", chosenTown.getNumResidents()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-resident.lore1"))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-resident.lore2"))
                .transform(i -> SkullCreator.itemWithBase64(i, skin));
        for (List<String> partition : partitions)
            item.lore(ChatColor.GRAY + partition.stream().reduce(((s1, s2) -> s1 + ", " + s2)).orElse(""));
        rightOptionPopulator.accept(item.buildItem().build());

        // place right option: town plots
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.GRASS_BLOCK)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-plots.name", "town_block_size", chosenTown.getTownBlocks().size()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-plots.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-plots.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-plots.lore3"))
                .buildItem().build());

        // place right option: town bankrupt status
        String bankrupt = TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.bankrupt");
        String nonBankrupt = TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.non-bankrupt");
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.SUNFLOWER)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.name", "bankrupt_status", chosenTown.isBankrupt() ? bankrupt : nonBankrupt))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bankrupt.lore3"))
                .buildItem().build());

        // place right option: town map
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.MAP)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-map.name"))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-map.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-map.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-map.lore3"))
                .buildItem().build());

        // place right option: town taxes
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.RAW_GOLD)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-taxes.name", "taxes", chosenTown.getTaxes(),
                        "is_tax_percentage", chosenTown.isTaxPercentage() ? TownyPortal.plugin.getMessage("gui.right") : TownyPortal.plugin.getMessage("gui.wrong")))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-taxes.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-taxes.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-taxes.lore3"))
                .buildItem().build());

        // place right option: town outposts
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.BIRCH_BOAT)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-outposts.name", "outpost_num", chosenTown.getAllOutpostSpawns().size()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-outposts.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-outposts.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-outposts.lore3"))
                .buildItem().build());

        // place right option: town bank
        rightOptionPopulator.accept(ItemStackBuilder.of(Material.CHEST)
                .name(TownyPortal.plugin.getMessage("gui.town-options.town-bank.name", "bank_balance", chosenTown.getAccount().getCachedBalance()))
                .lore("")
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bank.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bank.lore2"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.town-bank.lore3"))
                .buildItem().build());

        // place back button
        this.BACK_BUTTON.newPopulator(this.gui).accept(ItemStackBuilder.of(Material.REDSTONE)
                .name(TownyPortal.plugin.getMessage("gui.town-options.back.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.back.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.back.lore2"))
                .build(() -> this.gui.switchView((this.parentView))));
    }

}
