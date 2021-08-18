package co.mcsky.townyportal.gui.town;

import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.moecore.gui.SoundRegistry;
import co.mcsky.townyportal.TownyPortal;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.entity.Player;

public class TownListingGui extends SeamlessGui {

    // metadata key
    public static final MetadataKey<Town> CHOSEN_TOWN_KEY = MetadataKey.create("chosen-town", Town.class);

    public TownListingGui(Player player) {
        super(player, 6, TownyPortal.plugin.message(player, "gui.town-listing.title"), TownListingView::new);
        SoundRegistry.bindClickingSound(this);
        SoundRegistry.bindOpeningSound(this);
    }

}
