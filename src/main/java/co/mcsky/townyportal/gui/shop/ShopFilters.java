package co.mcsky.townyportal.gui.shop;

import co.mcsky.townyportal.data.ShopModel;
import me.lucko.helper.function.Predicates;

import java.util.function.Predicate;

public final class ShopFilters {

    public static Predicate<ShopModel> ALL() {
        return Predicates.alwaysTrue();
    }

    public static Predicate<ShopModel> BUY() {
        return ShopModel::hasBuyPrice;
    }

    public static Predicate<ShopModel> SELL() {
        return ShopModel::hasSellPrice;
    }

}
