package co.mcsky.townyportal.data;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a saved database.
 */
public class TownModelDatasource {

    private final Map<UUID, TownModel> townModelMap;

    public TownModelDatasource() {
        this.townModelMap = new HashMap<>();
        for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
            addTownModel(new TownModel(town.getUUID()));
        }
    }

    public void addTownModel(TownModel townModel) {
        this.townModelMap.put(townModel.getTownUUID(), townModel);
    }

    public TownModel getTownModel(UUID townUUID) {
        if (townModelMap.containsKey(townUUID)) {
            return townModelMap.get(townUUID);
        }
        TownModel added = new TownModel(townUUID);
        townModelMap.put(townUUID, added);
        return added;
    }

    public TownModel getTownModel(@NotNull Town town) {
        return getTownModel(town.getUUID());
    }

    public Map<UUID, TownModel> getTownModelMap() {
        return townModelMap;
    }

    public List<TownModel> getTownModels() {
        return townModelMap.values().stream().toList();
    }

    public void removeTownModel(UUID townUuid) {
        townModelMap.remove(townUuid);
    }

}
