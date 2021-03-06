package co.mcsky.townyportal.util;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TownMapBuilder {

    private static final Map<UUID, CooldownMap<Town>> cooldownMap = new HashMap<>();

    public static ItemStack createMapItem(Town town) {
        final MapView mapView = Bukkit.createMap(town.getWorld());
        try {
            mapView.setCenterX(town.getSpawn().getBlockX());
            mapView.setCenterZ(town.getSpawn().getBlockZ());
            mapView.setScale(MapView.Scale.valueOf(TownyPortal.config().town_map_scale.trim().toUpperCase()));
            mapView.setTrackingPosition(true);
            mapView.setUnlimitedTracking(true);
        } catch (TownyException e) {
            e.printStackTrace();
        }
        return ItemStackBuilder.of(Material.FILLED_MAP)
                .name(TownyPortal.text("town-map.map-title", "town", town.getName()))
                .transformMeta(meta -> {
                    MapMeta mapMeta = (MapMeta) meta;
                    mapMeta.setMapView(mapView);
                }).build();
    }

    public static void giveMap(Player player, Town town) {
        CooldownMap<Town> playerCooldownMap = cooldownMap.computeIfAbsent(player.getUniqueId(), k -> CooldownMap.create(Cooldown.of(1, TimeUnit.HOURS)));
        if (playerCooldownMap.test(town)) {
            final ItemStack mapItem = TownMapBuilder.createMapItem(town);
            player.getInventory().addItem(mapItem);
            player.sendMessage(TownyPortal.text("town-map.get-map-success", "town", town.getName()));
        } else {
            player.sendMessage(TownyPortal.text("town-map.already-obtained", "town", town.getName(), "timeout", playerCooldownMap.remainingTime(town, TimeUnit.MINUTES)));
        }
    }

}
