package co.mcsky.townyportal.gui.town;

import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.moecore.gui.SoundRegistry;
import co.mcsky.townyportal.TownyPortal;
import org.bukkit.entity.Player;

public class TownListingGui extends SeamlessGui {

    public TownListingGui(Player player) {
        super(player, 6, TownyPortal.plugin.getMessage(player, "gui.town-listing.title"),
                gui -> new TownListingView(gui, TownyPortal.plugin.getTownModelDatasource(), TownyPortal.plugin.getShopModelDatasource()));
        SoundRegistry.bindClickingSound(this);
        SoundRegistry.bindOpeningSound(this);
    }

}
