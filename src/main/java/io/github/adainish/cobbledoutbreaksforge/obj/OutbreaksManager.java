package io.github.adainish.cobbledoutbreaksforge.obj;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.config.Config;
import io.github.adainish.cobbledoutbreaksforge.util.Adapters;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OutbreaksManager
{
    public HashMap<Species, OutBreak> outBreakHashMap = new HashMap<>();

    public HashMap<String, OutBreakLocation> locationHashMap = new HashMap<>();

    public int maxOutBreaks = 5;

    public OutbreaksManager()
    {
        this.maxOutBreaks = CobbledOutBreaksForge.config.maxOutBreaks;
    }

    public void init()
    {
        if (!outBreakHashMap.isEmpty())
            shutdown();

        loadOutBreakLocations();
        generateOutBreaks();
    }

    public void loadOutBreakLocations() {
        if (CobbledOutBreaksForge.config != null) {
            Config config = CobbledOutBreaksForge.config;
            if (CobbledOutBreaksForge.getStorage().listFiles().length == 0) {
                config.initDefaultLocations();
            }
            //loop through files and turn into locations
            for (File f:CobbledOutBreaksForge.getStorage().listFiles()) {
                if (f != null)
                {
                    Gson gson = Adapters.PRETTY_MAIN_GSON;
                    JsonReader reader = null;

                    try {
                        reader = new JsonReader(new FileReader(f));
                        OutBreakLocation location = gson.fromJson(reader, OutBreakLocation.class);
                        if (location != null)
                            locationHashMap.put(location.id, location);
                    } catch (FileNotFoundException e) {

                    }
                }
            }

        }
    }

    public void generateOutBreaks() {
        if (CobbledOutBreaksForge.getServer() != null) {
            if (CobbledOutBreaksForge.getServer().getPlayerCount() <= 0) {
                return;
            }
            if (outBreakHashMap.values().size() >= maxOutBreaks)
                return;
            while (outBreakHashMap.values().size() < maxOutBreaks) {
                OutBreak outBreak = new OutBreak();
                outBreak.setSpecies();
                outBreak.time = CobbledOutBreaksForge.config.timerMinutes;

                //do announcement
                String msg = CobbledOutBreaksForge.config.broadcastMessage;

                outBreak.outBreakLocation = RandomHelper.removeRandomElementFromCollection(locationHashMap.values());
                outBreak.runnableTask = Task.builder().infinite().interval(20).execute(task -> {
                    outBreak.spawnPokemon();
                }).build();
                outBreak.started = System.currentTimeMillis();
                outBreakHashMap.put(outBreak.species, outBreak);
            }
        }
    }

    public void cleanExpiredOutBreaks()
    {
        List<Species> toremove = new ArrayList<>();
        outBreakHashMap.forEach((species, outBreak) -> {
            if (outBreak.expired()) {
                toremove.add(species);
            }
        });
        for (Species species : toremove) {
            OutBreak outBreak = outBreakHashMap.get(species);
            outBreak.runnableTask.setExpired();
            outBreakHashMap.remove(species);
        }
    }

    public void shutdown()
    {
        CobbledOutBreaksForge.getLog().warn("Shutting down all ongoing outbreaks");
        outBreakHashMap.forEach((species, outBreak) -> {
            outBreak.runnableTask.setExpired();
            outBreak.killAllOutBreakMons();
        });
        outBreakHashMap.clear();
        locationHashMap.clear();
    }

}
