package co.mcsky.townyportal.data;

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
    }

    public void setShops(List<ShopModel> shops) {
        shops.forEach(s -> shopMap.put(s.location(), s));
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
}
