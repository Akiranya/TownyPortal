package co.mcsky.townyportal.gui;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.gui.base.GuiView;
import co.mcsky.townyportal.gui.base.SeamlessGui;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;

import java.util.UUID;

public class ShopListingTownView extends ShopListingSimpleView {

    private static final MenuScheme BACK = new MenuScheme()
            .maskEmpty(5)
            .mask("000010000");
    private final GuiView parentView;

    public ShopListingTownView(GuiView parentView, SeamlessGui gui, ShopModelDatasource shopModelDataSource, UUID townUuid) {
        super(gui, shopModelDataSource);
        this.parentView = parentView;
        this.updateContent(s -> s.getTown().getUUID().equals(townUuid));
    }

    @Override
    public void renderSubview() {
        BACK.newPopulator(gui).accept(ItemStackBuilder.of(Material.REDSTONE)
                .name(TownyPortal.plugin.getMessage("gui.town-options.back.name"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.back.lore1"))
                .lore(TownyPortal.plugin.getMessage("gui.town-options.back.lore2"))
                .build(() -> gui.switchView(parentView)));
    }
}
