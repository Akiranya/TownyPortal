package co.mcsky.townyportal.data;

import co.mcsky.townyportal.config.YamlConfigFactory;
import co.mcsky.townyportal.serializer.LocationSerializer;
import co.mcsky.townyportal.serializer.ShopModelDatasourceSerializer;
import co.mcsky.townyportal.serializer.ShopModelSerializer;
import me.lucko.helper.serialize.FileStorageHandler;
import org.bukkit.Location;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class ShopModelFileHandler extends FileStorageHandler<ShopModelDatasource> {

    private final static String fileName = "shops";
    private final static String fileExt = ".yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public ShopModelFileHandler(File dataFolder) {
        super(fileName, fileExt, dataFolder);
        TypeSerializerCollection serializers = TypeSerializerCollection.defaults().childBuilder()
                .register(Location.class, new LocationSerializer())
                .register(ShopModel.class, new ShopModelSerializer())
                .register(ShopModelDatasource.class, new ShopModelDatasourceSerializer())
                .build();
        loader = YamlConfigFactory.loader(new File(dataFolder, fileName + fileExt));
        try {
            root = loader.load(loader.defaultOptions().serializers(serializers));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ShopModelDatasource readFromFile(Path path) {
        try {
            return root.get(ShopModelDatasource.class); // use root to get the node. don't use loader!
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void saveToFile(Path path, ShopModelDatasource shopModelDataSource) {
        try {
            loader.save(root.set(shopModelDataSource));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }
}
