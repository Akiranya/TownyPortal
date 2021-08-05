package co.mcsky.townyportal.data;

import co.mcsky.moecore.config.YamlConfigFactory;
import co.mcsky.townyportal.serializer.TownModelDatasourceSerializer;
import co.mcsky.townyportal.serializer.TownModelSerializer;
import me.lucko.helper.serialize.FileStorageHandler;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class TownModelFileHandler extends FileStorageHandler<TownModelDatasource> {

    private final static String fileName = "towns";
    private final static String fileExt = ".yml";
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public TownModelFileHandler(File dataFolder) {
        super(fileName, fileExt, dataFolder);
        TypeSerializerCollection serializers = YamlConfigFactory.typeSerializers().childBuilder()
                .register(TownModel.class, new TownModelSerializer())
                .register(TownModelDatasource.class, new TownModelDatasourceSerializer())
                .build();
        loader = YamlConfigurationLoader.builder()
                .file(new File(dataFolder, fileName + fileExt))
                .defaultOptions(opt -> opt.serializers(serializers))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
    }

    @Override
    protected TownModelDatasource readFromFile(Path path) {
        try {
            return (root = loader.load()).get(TownModelDatasource.class);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void saveToFile(Path path, TownModelDatasource townModelDataSource) {
        try {
            loader.save(root.set(townModelDataSource));
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

}
