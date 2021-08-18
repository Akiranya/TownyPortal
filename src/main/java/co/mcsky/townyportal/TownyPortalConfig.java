package co.mcsky.townyportal;

import co.mcsky.moecore.config.YamlConfigFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class TownyPortalConfig {

    public static final String fileName = "config.yml";

    /* config nodes start */

    public boolean debug;
    public int save_interval;

    public boolean shop_must_inside_plots;
    public boolean shop_must_inside_shop_plots;
    public int shop_icon_cache_timeout;

    public int town_board_max_line;
    public int resident_name_num_per_line;
    public boolean bypass_private_town_enabled;
    public int bypass_private_town_duration;

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

        debug = root.node("debug").getBoolean(false);
        save_interval = root.node("save-interval").getInt(1800);
        shop_must_inside_plots = root.node("shop-must-inside-plots").getBoolean(true);
        shop_must_inside_shop_plots = root.node("shop-must-inside-shop-plots").getBoolean(true);
        shop_icon_cache_timeout = root.node("shop-icon-cache-timeout").getInt(600);

        town_board_max_line = root.node("town-board-max-line").getInt(4);
        resident_name_num_per_line = root.node("resident-num-per-line").getInt(4);
        bypass_private_town_enabled = root.node("bypass-private-town").getBoolean(true);
        bypass_private_town_duration = root.node("private-bypass-duration").getInt(72);
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
