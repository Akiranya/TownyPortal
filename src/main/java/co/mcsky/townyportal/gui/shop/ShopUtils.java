package co.mcsky.townyportal.gui.shop;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.TownyUtils;
import co.mcsky.townyportal.data.ShopModel;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import net.ess3.api.InvalidWorldException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles misc utils of Towny & Shop GUI.
 */
public class ShopUtils {
    /**
     * Handles the logic to teleport player to specified shop.
     * <p>
     * If there is a warp named after the town of the {@param shopModel}, the
     * player will be directly teleported to the warp. Otherwise, the player
     * will be teleported to the spawn point of the town.
     *
     * @param player    the player
     * @param shopModel the shop model
     */
    public static void teleport(Player player, ShopModel shopModel) {
        final Town town = shopModel.getTown();
        final String townName = town.getName().toLowerCase(Locale.ROOT);

        try {
            // check warp
            TownyPortal.essentials().getWarps().getWarp(townName);

            // teleport player to warp
            final User user = TownyPortal.essentials().getUser(player);
            user.getAsyncTeleport().warp(user, townName, new Trade("warp", TownyPortal.essentials()), PlayerTeleportEvent.TeleportCause.PLUGIN, CompletableFuture.completedFuture(true));
        } catch (WarpNotFoundException | InvalidWorldException e) {

            // no warp named after the town
            TownyUtils.friendlyTownSpawn(player, town);
        }
    }

    /**
     * Sets a warp named after the town where the player stands in.
     * <p>
     * Fails if the town contains illegal chars.
     *
     * @param player the player who sets the warp
     */
    public static void setShopWarp(Player player) {
        Town town;
        if ((town = TownyAPI.getInstance().getTown(player.getLocation())) != null) {
            String townName = town.getName().toLowerCase(Locale.ROOT);
            final Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
            final Matcher matcher = pattern.matcher(townName);
            if (matcher.matches()) {
                try {
                    final User user = TownyPortal.essentials().getUser(player);
                    TownyPortal.essentials().getWarps().setWarp(user, townName, player.getLocation());
                    user.sendMessage(I18n.tl("warpSet", townName));
                } catch (Exception e) {
                    player.sendMessage(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                TownyPortal.text3("shop-teleport.illegal-town-name").to(player);
            }
        } else {
            TownyPortal.text3("shop-teleport.no-town-here").to(player);
        }
    }
}
