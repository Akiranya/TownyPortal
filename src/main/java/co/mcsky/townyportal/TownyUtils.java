package co.mcsky.townyportal;

import co.mcsky.townyportal.data.TownModel;
import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.command.TownCommand;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TownyUtils {

    /**
     * Teleports the player to the target town.
     * <p>
     * If the player is a newcomer (i.e. he joined the server within X hours),
     * then he will be teleported to the target town regardless of any
     * restrictions. Otherwise, normal teleportation will be used.
     *
     * @param player the player
     * @param town   the target town
     */
    public static void friendlyTownSpawn(Player player, Town town) {
        try {
            final Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            Preconditions.checkNotNull(resident, "resident");

            final long hoursSinceRegistered = TimeUnit.of(ChronoUnit.MILLIS).toHours(System.currentTimeMillis() - resident.getRegistered());
            if (TownyPortal.config().bypass_private_town_enabled || hoursSinceRegistered < TownyPortal.config().bypass_private_town_duration) {
                // bypass, or the player joined the server within X hours
                player.teleportAsync(town.getSpawn());
            } else {
                // the player is a senior
                TownCommand.townSpawn(player, new String[]{}, false, false);
            }
        } catch (TownyException e) {
            player.sendMessage(e.getMessage());
        }
    }

    public static boolean isMayor(UUID uuid) {
        Resident resident = TownyAPI.getInstance().getResident(uuid);
        if (resident == null) return false;
        return resident.isMayor();
    }

    public static Optional<TownModel> getTownModel(UUID residentUUID) {
        Resident resident = TownyAPI.getInstance().getResident(residentUUID);
        if (resident == null || !resident.hasTown()) return Optional.empty();
        try {
            UUID uuid = resident.getTown().getUUID();
            return Optional.of(TownyPortal.townModelDatasource().getTownModel(uuid));
        } catch (NotRegisteredException e) {
            return Optional.empty();
        }
    }

    public static TownModel getTownModelNullable(UUID residentUUID) {
        Resident resident = TownyAPI.getInstance().getResident(residentUUID);
        if (resident == null || !resident.hasTown()) return null;
        try {
            UUID townUuid = resident.getTown().getUUID();
            return TownyPortal.townModelDatasource().getTownModel(townUuid);
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    /**
     * Checks if the player is a resident of a given location
     *
     * @param player   Player to check
     * @param location Location
     * @return Is the player a resident of this location?
     */
    public static boolean isResident(Player player, Location location) {
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        try {
            if (townBlock != null) {
                return townBlock.getTown().hasResident(player.getName());
            }
            return false;
        } catch (NotRegisteredException ex) {
            return false;
        }
    }

    /**
     * Checks if the player is a resident of given locations
     *
     * @param player    Player to check
     * @param locations Locations
     * @return Is the player a resident of those locations?
     */
    public static boolean isResident(Player player, Location... locations) {
        for (Location location : locations) {
            if (!isResident(player, location)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the location is in wilderness
     *
     * @param location Location
     * @return Is the location in wilderness?
     */
    public static boolean isInWilderness(Location location) {
        return TownyAPI.getInstance().isWilderness(location);
    }

    /**
     * Checks if the locations are in wilderness
     *
     * @param locations Locations
     * @return Are the locations in wilderness?
     */
    public static boolean isInWilderness(Location... locations) {
        for (Location location : locations) {
            if (location != null && !isInWilderness(location)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the location is inside shop plot
     *
     * @param location Location to check
     * @return Is the location inside shop plot?
     */
    public static boolean isInsideShopPlot(Location location) {
        TownBlock townBlock = TownyAPI.getInstance().getTownBlock(location);
        if (townBlock != null) {
            return townBlock.getType() == TownBlockType.COMMERCIAL;
        }

        return false;
    }

    /**
     * Checks if the locations are inside shop plots
     *
     * @param locations Locations to check
     * @return Are the location inside shop plots?
     */
    public static boolean isInsideShopPlot(Location... locations) {
        for (Location location : locations) {
            if (location != null && !isInsideShopPlot(location)) {
                return false;
            }
        }

        return true;
    }

}
