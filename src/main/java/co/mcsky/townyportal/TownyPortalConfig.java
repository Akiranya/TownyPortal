package co.mcsky.townyportal;

import co.mcsky.moecore.config.YamlConfigFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;

public class TownyPortalConfig {

    public static final String fileName = "config.yml";

    private final YamlConfigurationLoader loader;

    /* config nodes start */

    public boolean debug;
    public int save_interval;

    public boolean shop_must_inside_plots;
    public boolean shop_must_inside_shop_plots;

    public int town_board_max_line;
    public int resident_name_num_per_line;

    public boolean bypass_private_town_enabled;
    public int bypass_private_town_duration;

    public String town_map_scale;

    /* config nodes end */

    private CommentedConfigurationNode root;

    public TownyPortalConfig() {
        loader = YamlConfigFactory.loader(new File(TownyPortal.plugin.getDataFolder(), fileName));
    }

    public void load() {
        try {
            root = loader.load();
        } catch (ConfigurateException e) {
            TownyPortal.logger().severe(e.getMessage());
            TownyPortal.plugin.getServer().getPluginManager().disablePlugin(TownyPortal.plugin);
        }

        /* initialize config nodes */

        final CommentedConfigurationNode internalNode = root.node("internal");
        debug = internalNode.node("debug").getBoolean(false);
        save_interval = internalNode.node("save-interval").getInt(1800);

        final CommentedConfigurationNode chestshopNode = root.node("chestshop");
        shop_must_inside_plots = chestshopNode.node("shop-must-inside-plots").getBoolean(true);
        shop_must_inside_shop_plots = chestshopNode.node("shop-must-inside-shop-plots").getBoolean(false);

        final CommentedConfigurationNode townGeneralNode = root.node("town", "general");
        town_map_scale = townGeneralNode.node("town-map-scale").getString("CLOSE");

        final CommentedConfigurationNode townboardNode = root.node("town", "board");
        town_board_max_line = townboardNode.node("town-board-max-line").getInt(4);
        resident_name_num_per_line = townboardNode.node("resident-num-per-line").getInt(4);

        final CommentedConfigurationNode townTeleportNode = root.node("town", "teleport");
        bypass_private_town_enabled = townTeleportNode.node("bypass-private-town-enabled").getBoolean(true);
        bypass_private_town_duration = townTeleportNode.node("bypass-private-town-duration").getInt(72);
    }

    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            TownyPortal.logger().severe(e.getMessage());
        }
    }

    public CommentedConfigurationNode root() {
        return root;
    }

}
