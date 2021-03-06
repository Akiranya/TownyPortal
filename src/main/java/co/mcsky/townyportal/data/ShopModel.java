package co.mcsky.townyportal.data;

import co.mcsky.townyportal.TownyPortal;
import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.Breeze.Utils.QuantityUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record ShopModel(UUID townUuid,
                        UUID ownerUuid,
                        String ownerName,
                        String[] lines,
                        Location location) {

    public int getQuantity() {
        return QuantityUtil.parseQuantity(lines[ChestShopSign.QUANTITY_LINE]);
    }

    public boolean hasBuyPrice() {
        return PriceUtil.hasBuyPrice(lines[ChestShopSign.PRICE_LINE]);
    }

    public boolean hasSellPrice() {
        return PriceUtil.hasSellPrice(lines[ChestShopSign.PRICE_LINE]);
    }

    public BigDecimal getBuyPrice() {
        return PriceUtil.getExactBuyPrice(lines[ChestShopSign.PRICE_LINE]);
    }

    public BigDecimal getSellPrice() {
        return PriceUtil.getExactSellPrice(lines[ChestShopSign.PRICE_LINE]);
    }

    public ItemStack getItem() {
        return MaterialUtil.getItem(lines[ChestShopSign.ITEM_LINE]);
    }

    public @Nullable Town getTown() {
        final Town town = TownyAPI.getInstance().getTown(townUuid);
        if (town == null) {
            TownyPortal.logger().severe("Null town uuid: %s".formatted(townUuid));
        }
        return town;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShopModel shopModel = (ShopModel) o;

        return location.equals(shopModel.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
