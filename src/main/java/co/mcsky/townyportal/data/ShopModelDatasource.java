package co.mcsky.townyportal.data;

import co.mcsky.townyportal.TownyPortal;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;

import java.util.*;

public class ShopModelDatasource {

    private final Map<Location, ShopModel> shopMap;

    public ShopModelDatasource() {
        this.shopMap = new HashMap<>();
    }

    public ShopModelDatasource(List<ShopModel> shops) {
        this.shopMap = new HashMap<>();
        shops.forEach(s -> shopMap.put(s.location(), s));

        // data cleanup: pass a null UUID so that it will only remove null towns
        removeShopModel(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    public void setShops(List<ShopModel> shops) {
        shops.forEach(s -> shopMap.put(s.location(), s));
    }

    public ShopModel getShopModel(Location location) {
        return shopMap.get(location);
    }

    public List<ShopModel> getShopList() {
        return shopMap.values().stream().toList();
    }

    public List<ShopModel> getShopListByTown(UUID town) {
        return shopMap.values().stream().filter(s -> s.townUuid().equals(town)).toList();
    }

    public void addShopModel(ShopModel model) {
        shopMap.put(model.location(), model);
    }

    public void removeShopModel(Location signLocation) {
        shopMap.remove(signLocation);
    }

    public void removeShopModel(UUID townUUID) {
        final Iterator<Map.Entry<Location, ShopModel>> iterator = shopMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Location, ShopModel> next = iterator.next();
            final Town town = next.getValue().getTown();
            if (town == null || town.getUUID().equals(townUUID)) {
                TownyPortal.logger().warning("[Data Cleanup] removing shop model (town UUID: %s)".formatted(townUUID));
                iterator.remove();
            }
        }
    }
}
