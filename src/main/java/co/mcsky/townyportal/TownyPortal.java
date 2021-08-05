package co.mcsky.townyportal;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.data.ShopModelFileHandler;
import co.mcsky.townyportal.data.TownModelDatasource;
import co.mcsky.townyportal.data.TownModelFileHandler;
import co.mcsky.townyportal.listener.ShopListener;
import co.mcsky.townyportal.listener.TownListener;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TownyPortal extends ExtendedJavaPlugin {

    public static TownyPortal plugin;
    public TownyPortalConfig config;
    public LanguageManager lang;

    private TownModelFileHandler townModelFileHandler;
    private ShopModelFileHandler shopModelFileHandler;
    private TownModelDatasource townModelDatasource;
    private ShopModelDatasource shopModelDatasource;

    @Override
    protected void enable() {
        plugin = this;

        this.config = new TownyPortalConfig();
        this.config.load();
        this.config.save();

        loadLanguages();
        registerCommands();

        // load data source from file
        townModelFileHandler = new TownModelFileHandler(new File(getDataFolder(), "data"));
        shopModelFileHandler = new ShopModelFileHandler(new File(getDataFolder(), "data"));
        townModelDatasource = townModelFileHandler.load().orElseGet(TownModelDatasource::new);
        shopModelDatasource = shopModelFileHandler.load().orElseGet(ShopModelDatasource::new);

        // schedule task to save data periodically
        Schedulers.async().runRepeating(() -> {
            townModelFileHandler.save(townModelDatasource);
            shopModelFileHandler.save(shopModelDatasource);
            getLogger().info("Datasource saved successfully!");
        }, 5, TimeUnit.SECONDS, this.config.save_interval, TimeUnit.SECONDS).bindWith(this);

        // register listeners
        bindModule(new TownListener(townModelDatasource));
        bindModule(new ShopListener(townModelDatasource, shopModelDatasource));

    }

    @Override
    protected void disable() {
        saveDataSource();
    }

    public void reloadConfig() {
        plugin.loadLanguages();
        plugin.config.load();
    }

    public void registerCommands() {
        PaperCommandManager commands = new PaperCommandManager(this);
        commands.registerCommand(new TownyPortalCommands(commands));
    }

    public void loadLanguages() {
        this.lang = new LanguageManager(this, "languages", "zh");
        this.lang.setPlaceholderPrefix("{");
        this.lang.setPlaceholderSuffix("}");
        this.lang.setProvider(sender -> {
            if (sender instanceof Player) {
                return ((Player) sender).locale().getLanguage();
            }
            return null;
        });
    }

    public String getMessage(CommandSender sender, String key, Object... replacements) {
        if (replacements.length == 0) {
            return lang.getConfig(sender).get(key);
        } else {
            String[] list = new String[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                list[i] = replacements[i].toString();
            }
            return lang.getConfig(sender).get(key, list);
        }
    }

    public String getMessage(String key, Object... replacements) {
        return getMessage(plugin.getServer().getConsoleSender(), key, replacements);
    }

    public TownModelDatasource getTownModelDatasource() {
        return townModelDatasource;
    }

    public ShopModelDatasource getShopModelDatasource() {
        return shopModelDatasource;
    }

    public boolean isDebugMode() {
        return config.debug;
    }

    /**
     * Loads the data source from file.
     * <p>
     * CAUTION: This will overwrite all the data in the memory!
     */
    public void loadDataSource() {
        // TODO async IO
        townModelDatasource = townModelFileHandler.load().orElseGet(TownModelDatasource::new);
        shopModelDatasource = shopModelFileHandler.load().orElseGet(ShopModelDatasource::new);
    }

    public void saveDataSource() {
        // TODO async IO
        townModelFileHandler.saveAndBackup(townModelDatasource);
        shopModelFileHandler.saveAndBackup(shopModelDatasource);
    }
}
