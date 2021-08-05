package co.mcsky.townyportal.gui.shop;

import co.mcsky.moecore.gui.GuiView;
import co.mcsky.moecore.gui.SeamlessGui;
import co.mcsky.townyportal.TownyPortal;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

import java.util.UUID;

public class ShopListingTownView extends ShopListingSimpleView {

    private static final MenuScheme BACK = new MenuScheme()
            .maskEmpty(5)
            .mask("000010000");
    private final GuiView parentView;

    public ShopListingTownView(GuiView parentView, SeamlessGui gui, UUID townUuid) {
        super(gui);
        this.parentView = parentView;
        this.updateContent(s -> s.getTown().getUUID().equals(townUuid));
    }

    @Override
    public void renderSubview() {
        BACK.newPopulator(gui).accept(ItemStackBuilder.of(Material.REDSTONE)
                .name(TownyPortal.plugin.message("gui.town-options.back.name"))
                .lore(TownyPortal.plugin.message("gui.town-options.back.lore1"))
                .lore(TownyPortal.plugin.message("gui.town-options.back.lore2"))
                .build(() -> gui.switchView(parentView)));
    }
}
