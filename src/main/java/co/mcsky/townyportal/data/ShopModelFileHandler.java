package co.mcsky.townyportal.data;

import co.mcsky.moecore.config.YamlConfigFactory;
import co.mcsky.townyportal.serializer.ShopModelDatasourceSerializer;
import co.mcsky.townyportal.serializer.ShopModelSerializer;
import me.lucko.helper.serialize.FileStorageHandler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
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
        TypeSerializerCollection serializers = YamlConfigFactory.typeSerializers().childBuilder()
                .register(ShopModel.class, new ShopModelSerializer())
                .register(ShopModelDatasource.class, new ShopModelDatasourceSerializer())
                .build();
        loader = YamlConfigurationLoader.builder()
                .file(new File(dataFolder, fileName + fileExt))
                .defaultOptions(opt -> opt.serializers(serializers))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
    }

    @Override
    protected ShopModelDatasource readFromFile(Path path) {
        try {
            return (root = loader.load()).get(ShopModelDatasource.class); // use root to get the node. don't use loader!
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void saveToFile(Path path, ShopModelDatasource shopModelDatasource) {
        try {
            loader.save(root.set(shopModelDatasource));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }
}
