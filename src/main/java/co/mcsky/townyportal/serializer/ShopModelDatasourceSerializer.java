package co.mcsky.townyportal.serializer;

import co.mcsky.townyportal.data.ShopModel;
import co.mcsky.townyportal.data.ShopModelDatasource;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShopModelDatasourceSerializer implements TypeSerializer<ShopModelDatasource> {

    @Override
    public ShopModelDatasource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final List<ShopModel> shops = node.node("shops").getList(ShopModel.class, new ArrayList<>());
        Preconditions.checkNotNull(shops);
        return new ShopModelDatasource(shops);
    }

    @Override
    public void serialize(Type type, @Nullable ShopModelDatasource obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("shops").setList(ShopModel.class, obj.getShopList());
    }
}
