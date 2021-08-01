package co.mcsky.townyportal.gui;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.gui.base.SeamlessGui;
import co.mcsky.townyportal.gui.base.SoundRegistry;
import org.bukkit.entity.Player;

public class ShopListingGui extends SeamlessGui {

    public ShopListingGui(Player player) {
        super(player, 6, TownyPortal.plugin.getMessage(player, "gui.shop-listing.title"),
                gui -> new ShopListingFilterView(gui, TownyPortal.plugin.getShopModelDatasource()));
        SoundRegistry.bindClickingSound(this);
        SoundRegistry.bindOpeningSound(this);
    }
}
