package co.mcsky.townyportal.serializer;

import co.mcsky.townyportal.data.TownModel;
import co.mcsky.townyportal.data.TownModelDatasource;
import com.google.common.base.Preconditions;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class TownModelDatasourceSerializer implements TypeSerializer<TownModelDatasource> {

    @Override
    public TownModelDatasource deserialize(Type type, ConfigurationNode node) throws SerializationException {
        TownModelDatasource townModelDatasource = new TownModelDatasource();
        ConfigurationNode townsNode = node.node("towns");
        List<TownModel> list = townsNode.getList(TypeToken.get(TownModel.class));
        if (list != null) {
            for (TownModel townModel : list) {
                townModelDatasource.addTownModel(townModel);
            }
        }
        return townModelDatasource;
    }

    @Override
    public void serialize(Type type, @Nullable TownModelDatasource townModelDatasource, ConfigurationNode node) throws
            SerializationException {
        Preconditions.checkNotNull(townModelDatasource);
        node.node("version").set(1);
        node.node("towns").setList(TownModel.class, townModelDatasource.getTownModels());
    }

}
