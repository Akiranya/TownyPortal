package co.mcsky.townyportal;

import co.mcsky.townyportal.config.YamlConfigFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class TownyPortalConfig {

    public static final String fileName = "config.yml";

    /* config nodes start */

    public boolean debug;
    public int save_interval;
    public int town_board_max_line;
    public boolean shop_must_inside_plots;
    public boolean shop_must_inside_shop_plots;
    public int resident_name_num_per_line;

    /* config nodes end */

    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public TownyPortalConfig() {
        loader = YamlConfigFactory.loader(new File(TownyPortal.plugin.getDataFolder(), fileName));
    }

    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            TownyPortal.plugin.getLogger().severe(e.getMessage());
            TownyPortal.plugin.getServer().getPluginManager().disablePlugin(TownyPortal.plugin);
        }

        /* initialize config nodes */

        debug = root.node("debug")
                .comment("Generate more output to console")
                .getBoolean(false);
        save_interval = root.node("save-interval")
                .comment("How often to save data into file")
                .getInt(1800);
        town_board_max_line = root.node("town-board-max-line")
                .comment("The max number of lines allowed in town board command")
                .getInt(4);
        shop_must_inside_plots = root.node("shop-must-inside-plots")
                .comment("Should people only be able to build shops inside plots?")
                .getBoolean(true);
        shop_must_inside_shop_plots = root.node("shop-must-inside-shop-plots")
                .comment("Should people only be able to build shops inside commercial plots?")
                .getBoolean(true);
        resident_name_num_per_line = root.node("resident-num-per-line")
                .comment("The number of resident names per line in the GUI")
                .getInt(4);
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            TownyPortal.plugin.getLogger().severe(e.getMessage());
        }
    }

    public CommentedConfigurationNode root() {
        return root;
    }

}
