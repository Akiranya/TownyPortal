package co.mcsky.townyportal.listener;

import co.mcsky.townyportal.TownyPortal;
import co.mcsky.townyportal.data.TownModel;
import co.mcsky.townyportal.data.TownModelDatasource;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.object.Town;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class TownListener implements TerminableModule {

    private final TownModelDatasource townModelDataSource;

    public TownListener(TownModelDatasource townModelDataSource) {
        this.townModelDataSource = townModelDataSource;
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        // handle town model data source
        Events.subscribe(NewTownEvent.class).handler(e -> {
            if (TownyPortal.plugin.isDebugMode()) {
                TownyPortal.plugin.getLogger().info("New town %s created, adding town model to data source".formatted(e.getTown().getName()));
            }
            townModelDataSource.addTownModel(new TownModel(e.getTown().getUUID()));
        }).bindWith(consumer);
        Events.subscribe(DeleteTownEvent.class).handler(e -> {
            if (TownyPortal.plugin.isDebugMode()) {
                TownyPortal.plugin.getLogger().info("Town %s deleted, removing town model from data source".formatted(e.getTownName()));
            }
            townModelDataSource.removeTownModel(e.getTownUUID());
        }).bindWith(consumer);

        // handle towny's bug: default public setting doesn't work
        Events.subscribe(NewTownEvent.class).handler(e -> {
            final Town town = e.getTown();
            town.setPublic(true); // set town's public status to be true
        }).bindWith(consumer);
    }

}
