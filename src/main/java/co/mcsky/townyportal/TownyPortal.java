package co.mcsky.townyportal;

import co.aikar.commands.PaperCommandManager;
import co.mcsky.moecore.text.Text;
import co.mcsky.moecore.text.TextRepository;
import co.mcsky.townyportal.data.ShopModelDatasource;
import co.mcsky.townyportal.data.ShopModelFileHandler;
import co.mcsky.townyportal.data.TownModelDatasource;
import co.mcsky.townyportal.data.TownModelFileHandler;
import co.mcsky.townyportal.gui.shop.ShopItemCache;
import co.mcsky.townyportal.listener.ShopListener;
import co.mcsky.townyportal.listener.TownListener;
import de.themoep.utils.lang.bukkit.LanguageManager;
import me.lucko.helper.Schedulers;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TownyPortal extends ExtendedJavaPlugin {

    public static TownyPortal plugin;

    private TownyPortalConfig config;
    private LanguageManager languageManager;
    private TextRepository textRepository;
    private TownModelFileHandler townModelFileHandler;
    private ShopModelFileHandler shopModelFileHandler;
    private TownModelDatasource townModelDatasource;
    private ShopModelDatasource shopModelDatasource;

    private IEssentials essentials;

    public static Logger logger() {
        return plugin.getLogger();
    }

    public static TownyPortalConfig config() {
        return plugin.config;
    }

    public static TownModelDatasource townModelDatasource() {
        return plugin.townModelDatasource;
    }

    public static ShopModelDatasource shopModelDatasource() {
        return plugin.shopModelDatasource;
    }

    public static String text(String key, Object... replacements) {
        if (replacements.length == 0) {
            return plugin.languageManager.getDefaultConfig().get(key);
        } else {
            String[] list = new String[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                list[i] = replacements[i].toString();
            }
            return plugin.languageManager.getDefaultConfig().get(key, list);
        }
    }

    public static Text text3(String key) {
        return plugin.textRepository.get(key);
    }

    public static IEssentials essentials() {
        return plugin.essentials;
    }

    @Override
    protected void enable() {
        plugin = this;

        this.config = new TownyPortalConfig();
        this.config.load();
        this.config.save();

        // load essentials
        if ((essentials = getPlugin("Essentials", IEssentials.class)) == null) {
            getLogger().severe("Essentials is not loaded");
            disable();
            return;
        }

        loadLanguages();
        registerCommands();

        // load data source from file
        townModelFileHandler = new TownModelFileHandler(new File(getDataFolder(), "data"));
        shopModelFileHandler = new ShopModelFileHandler(new File(getDataFolder(), "data"));
        townModelDatasource = townModelFileHandler.load().orElseGet(TownModelDatasource::new);
        shopModelDatasource = shopModelFileHandler.load().orElseGet(ShopModelDatasource::new);

        // schedule task to save data periodically
        Schedulers.async().runRepeating(() -> {
            townModelFileHandler.save(townModelDatasource());
            shopModelFileHandler.save(shopModelDatasource());
            getLogger().info("Datasource saved successfully!");
        }, 5, TimeUnit.SECONDS, this.config.save_interval, TimeUnit.SECONDS).bindWith(this);


        // register listeners
        bindModule(new TownListener());
        bindModule(new ShopListener());

    }

    @Override
    protected void disable() {
        saveDatasource();
    }

    public void reload() {
        plugin.loadLanguages();
        plugin.config.load();
        ShopItemCache.clear();
    }

    private void registerCommands() {
        PaperCommandManager commands = new PaperCommandManager(this);
        commands.registerCommand(new TownyPortalCommands(commands));
    }

    private void loadLanguages() {
        this.languageManager = new LanguageManager(this, "languages", "zh");
        this.languageManager.setPlaceholderPrefix("{");
        this.languageManager.setPlaceholderSuffix("}");
        this.languageManager.setProvider(sender -> {
            if (sender instanceof Player) {
                return ((Player) sender).locale().getLanguage();
            }
            return null;
        });
        textRepository = new TextRepository(TownyPortal::text);
    }

    private void hookExternal() {

    }

    /**
     * Loads the data source from file, overwriting the data in memory.
     */
    public void loadDatasource() {
        townModelDatasource = townModelFileHandler.load().orElseGet(TownModelDatasource::new);
        shopModelDatasource = shopModelFileHandler.load().orElseGet(ShopModelDatasource::new);
    }

    /**
     * Saves the data into file, overwriting all the contents in the file.
     */
    public void saveDatasource() {
        townModelFileHandler.saveAndBackup(townModelDatasource);
        shopModelFileHandler.saveAndBackup(shopModelDatasource);
    }
}
