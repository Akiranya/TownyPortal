package co.mcsky.townyportal.gui.shop;

import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.moecore.gui.SoundRegistry;
import co.mcsky.townyportal.TownyPortal;
import org.bukkit.entity.Player;

public class ShopListingGui extends SeamlessGui {

    public ShopListingGui(Player player) {
        super(player, 6, TownyPortal.plugin.getMessage(player, "gui.shop-listing.title"),
                gui -> new ShopListingFilterView(gui, TownyPortal.plugin.getShopModelDatasource()));
        SoundRegistry.bindClickingSound(this);
        SoundRegistry.bindOpeningSound(this);
    }
}
