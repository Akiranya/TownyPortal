package co.mcsky.townyportal.listener;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.TownyUtils;
import co.mcsky.townyportal.data.ShopModel;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.data.TownModel;
import co.mcsky.townyportal.data.TownModelDatasource;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;

public class ShopListener implements TerminableModule {

    private final TownModelDatasource townModelDatasource;
    private final ShopModelDatasource shopModelDatasource;
    private final TownyAPI townyAPI;

    public ShopListener(TownModelDatasource townModelDatasource, ShopModelDatasource shopModelDatasource) {
        this.townModelDatasource = townModelDatasource;
        this.shopModelDatasource = shopModelDatasource;
        this.townyAPI = TownyAPI.getInstance();
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        // control whether the chest shop signs should be created inside towns
        Events.subscribe(BuildPermissionEvent.class).handler(e -> {
            if (e.getPlayer().hasPermission("chestshop.towny.bypass")) {
                e.allow();
                return;
            }

            Location sign = e.getSign();
            if ((TownyPortal.plugin.config.shop_must_inside_plots && TownyUtils.isInWilderness(sign))
                    || (TownyPortal.plugin.config.shop_must_inside_shop_plots && !TownyUtils.isInsideShopPlot(sign))) {
                e.disallow();
                return;
            }
            e.allow();
        }).bindWith(consumer);

        // increase the shop num in town model
        Events.subscribe(ShopCreatedEvent.class).handler(e -> {
            // get shop sign location
            Sign sign = e.getSign();
            Location signLocation = sign.getBlock().getLocation();

            Town town = townyAPI.getTown(signLocation);
            if (town == null) return;

            // increment shop num
            if (TownyPortal.plugin.isDebugMode())
                TownyPortal.plugin.getLogger().info("Town %s's shop num increment".formatted(town.getName()));
            TownModel townModel = townModelDatasource.getTownModel(town.getUUID());
            townModel.incrementShopNum();
        }).bindWith(consumer);

        // decrease the shop num in town model
        Events.subscribe(ShopDestroyedEvent.class).handler(e -> {
            // get shop sign location
            final Sign sign = e.getSign();
            Location signLocation = sign.getBlock().getLocation();

            Town town = townyAPI.getTown(signLocation);
            if (town == null) return;

            // decrement shop num
            if (TownyPortal.plugin.isDebugMode())
                TownyPortal.plugin.getLogger().info("Town %s's shop num decrement".formatted(town.getName()));
            TownModel townModel = townModelDatasource.getTownModel(town.getUUID());
            townModel.decrementShopNum();
        }).bindWith(consumer);

        // add shop model to the database when a shop is created
        Events.subscribe(ShopCreatedEvent.class)
                .filter(e -> !ChestShopSign.isAdminShop(e.getSign())) // ignore admin shop creation
                .handler(e -> {
                    final Sign sign = e.getSign();
                    final Location signLocation = sign.getBlock().getLocation();

                    final Town town = townyAPI.getTown(signLocation);
                    if (town == null) {
                        if (TownyPortal.plugin.isDebugMode()) {
                            TownyPortal.plugin.getLogger().info("Adding shop model aborted: null town");
                        }
                        return;
                    }

                    final ShopModel shopModel = new ShopModel(town.getUUID(), e.getPlayer().getUniqueId(), e.getSignLines(), sign.getLocation());
                    shopModelDatasource.addShopModel(shopModel);
                    if (TownyPortal.plugin.isDebugMode()) {
                        TownyPortal.plugin.getLogger().info("Added shop model to datasource successfully");
                    }
                }).bindWith(consumer);

        // remove shop model from the data source when a shop is destroyed
        Events.subscribe(ShopDestroyedEvent.class)
                .filter(e -> !ChestShopSign.isAdminShop(e.getSign())) // ignore admin shop creation
                .handler(e -> {
                    final Sign sign = e.getSign();
                    final Location signLocation = sign.getBlock().getLocation();
                    shopModelDatasource.removeShopModel(signLocation);
                    if (TownyPortal.plugin.isDebugMode()) {
                        TownyPortal.plugin.getLogger().info("Removed shop model from datasource");
                    }
                }).bindWith(consumer);
    }

}
