package co.mcsky.townyportal.map;

import co.mcsky.townyportal.TownyPortal;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.concurrent.TimeUnit;

public class TownMapBuilder {

    private static final CooldownMap<Town> cooldownMap = CooldownMap.create(Cooldown.of(1, TimeUnit.HOURS));

    public static ItemStack createMapItem(Town town) {
        final MapView mapView = Bukkit.createMap(town.getWorld());
        try {
            mapView.setCenterX(town.getSpawn().getBlockX());
            mapView.setCenterZ(town.getSpawn().getBlockZ());
            mapView.setScale(MapView.Scale.NORMAL);
            mapView.setTrackingPosition(true);
            mapView.setUnlimitedTracking(true);
        } catch (TownyException e) {
            e.printStackTrace();
        }
        return ItemStackBuilder.of(Material.FILLED_MAP)
                .name(TownyPortal.plugin.message("town-map.map-title", "town", town.getName()))
                .transformMeta(meta -> {
                    MapMeta mapMeta = (MapMeta) meta;
                    mapMeta.setMapView(mapView);
                }).build();
    }

    public static void giveMap(Player player, Town town) {
        if (cooldownMap.test(town)) {
            final ItemStack mapItem = TownMapBuilder.createMapItem(town);
            player.getInventory().addItem(mapItem);
            player.sendMessage(TownyPortal.plugin.message("town-map.get-map-success", "town", town.getName()));
        } else {
            player.sendMessage(TownyPortal.plugin.message("town-map.already-obtained", "town", town.getName(), "timeout", cooldownMap.remainingTime(town, TimeUnit.MINUTES)));
        }
    }

}
