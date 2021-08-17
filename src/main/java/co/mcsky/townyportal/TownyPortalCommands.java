package co.mcsky.townyportal;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import co.mcsky.townyportal.data.TownModel;
import co.mcsky.townyportal.gui.shop.ShopListingGui;
import co.mcsky.townyportal.gui.town.TownListingGui;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CommandAlias("towns|ts")
public class TownyPortalCommands extends BaseCommand {

    private final PaperCommandManager commands;

    public TownyPortalCommands(PaperCommandManager commands) {
        this.commands = commands;
        registerCompletions();
        registerConditions();
    }

    private void registerCompletions() {
        commands.getCommandCompletions().registerCompletion("world", c -> Bukkit.getWorlds().stream().map(World::getName).toList());
        commands.getCommandCompletions().registerCompletion("line", c -> {
            Player player = c.getPlayer();
            Optional<TownModel> townModel = TownyUtils.getTownModel(player.getUniqueId());
            if (townModel.isPresent()) {
                List<String> completions = new ArrayList<>();
                for (int i = 1; i < townModel.get().getTownBoardNum() + 1; i++) {
                    completions.add(String.valueOf(i));
                }
                return completions;
            } else {
                return List.of("-1");
            }
        });
        commands.getCommandCompletions().registerCompletion("shop_type", c -> List.of("sell", "buy", "all"));
    }

    private void registerConditions() {
        commands.getCommandConditions().addCondition("is_mayor", c -> {
            if (!TownyUtils.isMayor(c.getIssuer().getUniqueId())) {
                throw new ConditionFailedException(TownyPortal.plugin.message(c.getIssuer().getPlayer(), "chat-message.only-mayor-can-use"));
            }
        });
    }

    @Default
    @Subcommand("towns")
    public void openTownGui(Player player) {
        new TownListingGui(player).open();
    }

    @Subcommand("shops")
    @CommandCompletion("@shop_type")
    public void openShopGui(Player player, String shopType) {
        new ShopListingGui(player, shopType.trim().toLowerCase()).open();
    }

    @Subcommand("reloadconfig")
    @CommandPermission("townyportal.admin")
    public void reloadConfig(CommandSender sender) {
        TownyPortal.plugin.reload();
        sender.sendMessage(TownyPortal.plugin.message(sender, "chat-message.plugin-config-reloaded"));
    }

    @Subcommand("datasource")
    @CommandPermission("townyportal.admin")
    class DatasourceCommand extends BaseCommand {

        @Subcommand("load")
        public void load(CommandSender sender) {
            TownyPortal.plugin.loadDatasource();
            sender.sendMessage(TownyPortal.plugin.message(sender, "chat-message.plugin-datasource-loaded"));
        }

        @Subcommand("save")
        public void save(CommandSender sender) {
            TownyPortal.plugin.saveDatasource();
            sender.sendMessage(TownyPortal.plugin.message(sender, "chat-message.plugin-datasource-saved"));
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Subcommand("board")
    @Conditions("is_mayor")
    class BoardCommand extends BaseCommand {

        @Default
        public void help(Player player) {
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.help.board-view"));
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.help.board-add"));
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.help.board-set"));
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.help.board-delete"));
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.help.board-clear"));
        }

        @Subcommand("view")
        public void view(Player player) {
            TownModel townModel = TownyUtils.getTownModelNullable(player.getUniqueId());
            int index = 1;
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-view-title"));
            for (String l : townModel.getTownBoard()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', index++ + ". " + l));
            }
        }

        @Subcommand("add")
        @Syntax("[文本]")
        @CommandCompletion("@nothing")
        public void add(Player player, String text) {
            TownModel townModel = TownyUtils.getTownModelNullable(player.getUniqueId());
            if (townModel.getTownBoardNum() >= TownyPortal.plugin.config.town_board_max_line) {
                player.sendMessage(TownyPortal.plugin.message(player, "chat-message.exceed-town-board-max-line"));
            } else {
                townModel.addTownBoard(text);
                view(player);
                player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-updated"));
            }
        }

        @Subcommand("set")
        @Syntax("[行数] [文本]")
        @CommandCompletion("@line @nothing")
        public void set(Player player, Integer line, String text) {
            TownModel townModel = TownyUtils.getTownModelNullable(player.getUniqueId());
            if (line < 1 || line > townModel.getTownBoardNum()) {
                player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-out-of-index"));
                return;
            }
            townModel.setTownBoard(line - 1, text);
            view(player);
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-updated"));
        }

        @Subcommand("delete")
        @Syntax("[行数]")
        @CommandCompletion("@line")
        public void delete(Player player, Integer line) {
            TownModel townModel = TownyUtils.getTownModelNullable(player.getUniqueId());
            if (line < 1 || line > townModel.getTownBoardNum()) {
                player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-out-of-index"));
                return;
            }
            townModel.deleteTownBoard(line - 1);
            view(player);
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-updated"));
        }

        @Subcommand("clear")
        public void clear(Player player) {
            TownModel townModel = TownyUtils.getTownModelNullable(player.getUniqueId());
            townModel.clearTownBoard();
            player.sendMessage(TownyPortal.plugin.message(player, "chat-message.town-board-cleared"));
        }

    }

}
