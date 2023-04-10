package io.github.adainish.cobbledoutbreaksforge.obj;

import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.config.Config;
import io.github.adainish.cobbledoutbreaksforge.scheduler.AsyncScheduler;
import io.github.adainish.cobbledoutbreaksforge.util.Adapters;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import io.github.adainish.cobbledoutbreaksforge.util.Util;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OutbreaksManager
{
    public HashMap<Species, OutBreak> outBreakHashMap = new HashMap<>();

    public HashMap<String, OutBreakLocation> locationHashMap = new HashMap<>();

    public int maxOutBreaks = 5;

    public long lastOutBreak = 0;

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

    public List<String> alphabet()
    {
        return new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    }


    public String randomIDGenerator()
    {
        StringBuilder stringBuilder = new StringBuilder("AutoID");
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(RandomHelper.getRandomElementFromCollection(alphabet()));
        }
        return stringBuilder.toString();
    }

    public OutBreakLocation getRandomOutBreakLocation()
    {
        OutBreakLocation location = RandomHelper.getRandomElementFromCollection(locationHashMap.values());
        //check if config
        if (CobbledOutBreaksForge.config.usePlayerLocations) {
            //yes? select random player
            ServerPlayer player = RandomHelper.getRandomElementFromCollection(CobbledOutBreaksForge.getServer().getPlayerList().getPlayers());
            //make outbreak location from player
            if (player != null)
            {
                location = new OutBreakLocation();
                location.id = randomIDGenerator();
                location.minX = player.getX() - 15;
                location.maxX = player.getX() + 35;
                location.minY = 0;
                location.maxY = 255;
                location.minZ = player.getZ() - 15;
                location.maxZ = player.getZ() + 35;
            }
        }


        return location;
    }

    public boolean canCreateNewOutBreak()
    {
        return System.currentTimeMillis() >= (lastOutBreak + TimeUnit.MINUTES.toMillis(CobbledOutBreaksForge.config.delayMinutes));
    }

    public void generateOutBreaks() {
        if (CobbledOutBreaksForge.getServer() != null) {
            if (CobbledOutBreaksForge.getServer().getPlayerCount() <= 0) {
                return;
            }
            if (outBreakHashMap.values().size() >= maxOutBreaks)
                return;
            if (!canCreateNewOutBreak())
                return;
            while (outBreakHashMap.values().size() < maxOutBreaks) {
                OutBreak outBreak = new OutBreak();
                outBreak.setSpecies();
                if (outBreakHashMap.containsKey(outBreak.species))
                    continue;

                outBreak.time = CobbledOutBreaksForge.config.timerMinutes;
                outBreak.outBreakLocation = getRandomOutBreakLocation();
                if (outBreak.outBreakLocation == null)
                    continue;
                outBreak.shinyChance = CobbledOutBreaksForge.config.shinyChance;
                //do announcement
                String msg = CobbledOutBreaksForge.config.broadcastMessage;
                Util.doBroadcast(msg
                        .replace("%species%", outBreak.species.getName())
                                .replace("%time%", outBreak.timeLeft())
                        .replace("%location%", outBreak.outBreakLocation.prettyLocation()));

                AsyncScheduler.Builder builder = new AsyncScheduler.Builder();
                builder.withConsumerTask(c -> {
                    outBreak.spawnPokemon();
                });
                builder.withInfiniteIterations();
                builder.withInterval(20);
                AsyncScheduler scheduler = builder.build();
                scheduler.start();
                outBreak.scheduler = scheduler;
                outBreak.started = System.currentTimeMillis();
                outBreakHashMap.put(outBreak.species, outBreak);
            }
        } else {
            CobbledOutBreaksForge.getLog().warn("Could not load server instance, failed to start outbreaks");
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
            outBreak.scheduler.stop();
            outBreak.killAllOutBreakMons();
            if (!CobbledOutBreaksForge.config.finishedBroadCastMessage.isBlank())
                Util.doBroadcast(CobbledOutBreaksForge.config.finishedBroadCastMessage.replace("%species%", species.getName()));
            outBreakHashMap.remove(species);
            this.lastOutBreak = System.currentTimeMillis();
        }
    }

    public void shutdown()
    {
        CobbledOutBreaksForge.getLog().warn("Shutting down all ongoing outbreaks");
        outBreakHashMap.forEach((species, outBreak) -> {
            outBreak.scheduler.stop();
            outBreak.killAllOutBreakMons();
        });
        outBreakHashMap.clear();
        locationHashMap.clear();
    }

}
