package co.mcsky.townyportal.serializer;

import co.mcsky.townyportal.data.TownModel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TownModelSerializer implements TypeSerializer<TownModel> {

    @Override
    public TownModel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        UUID uuid = node.node("uuid").get(UUID.class);
        List<String> townBoard = node.node("town-board").getList(String.class);
        int shopNum = node.node("shop-num").getInt();

        if (townBoard == null) {
            townBoard = new LinkedList<>();
        }

        return new TownModel(uuid, townBoard, shopNum);
    }

    @Override
    public void serialize(Type type, @Nullable TownModel townModel, ConfigurationNode node) throws
            SerializationException {
        if (townModel == null) {
            throw new SerializationException("townModel == null");
        }
        node.node("uuid").set(townModel.getTownUUID());
        node.node("town-board").set(townModel.getTownBoard());
        node.node("shop-num").set(townModel.getShopNum());
    }

}
