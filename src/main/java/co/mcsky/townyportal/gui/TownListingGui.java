package co.mcsky.townyportal.gui;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.gui.base.SeamlessGui;
import co.mcsky.townyportal.gui.base.SoundRegistry;
import org.bukkit.entity.Player;

public class TownListingGui extends SeamlessGui {

    public TownListingGui(Player player) {
        super(player, 6, TownyPortal.plugin.getMessage(player, "gui.town-listing.title"), gui -> new TownListingView(gui, TownyPortal.plugin.getTownModelDatasource()));
        SoundRegistry.bindClickingSound(this);
        SoundRegistry.bindOpeningSound(this);
    }

}
