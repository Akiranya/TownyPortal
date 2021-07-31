package co.mcsky.townyportal.serializer;

import co.mcsky.townyportal.data.ShopModel;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopModelSerializer implements TypeSerializer<ShopModel> {

    @Override
    public ShopModel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final UUID town = node.node("town").get(UUID.class);
        final UUID owner = node.node("owner").get(UUID.class);
        final List<String> lines = node.node("sign").getList(String.class, new ArrayList<>());
        final Location location = node.node("location").get(Location.class);
        return new ShopModel(town, owner, lines.toArray(new String[0]), location);
    }

    @Override
    public void serialize(Type type, @Nullable ShopModel obj, ConfigurationNode node) throws SerializationException {
        Preconditions.checkNotNull(obj);
        node.node("town").set(obj.townUuid());
        node.node("owner").set(obj.ownerUuid());
        node.node("sign").setList(String.class, List.of(obj.lines()));
        node.node("location").set(obj.location());
    }
}
