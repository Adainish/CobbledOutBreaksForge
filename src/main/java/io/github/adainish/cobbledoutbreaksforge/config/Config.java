package io.github.adainish.cobbledoutbreaksforge.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.obj.OutBreakLocation;
import io.github.adainish.cobbledoutbreaksforge.util.Adapters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config
{
    public boolean usePlayerLocations = false;
    public int maxOutBreaks = 0;
    public int shinyChance = 0;
    public int timerMinutes = 0;
    public String broadcastMessage = "";
    public String timerPlaceHolder = "";
    public String locationPlaceHolder = "";
    public Config()
    {
        this.usePlayerLocations = false;
        this.maxOutBreaks = 5;
        this.shinyChance = 1;
        this.timerMinutes = 5;
        this.broadcastMessage = "&cAn outbreak has started with the pokemon %species% for %time% at %location%";
        this.timerPlaceHolder = "%hours% hours and %minutes% minutes";
        this.locationPlaceHolder = "Location: %minx% - %maxx%x %miny%-%maxy%y %minz%-%maxz%z";
    }

    public static void writeConfig()
    {
        File dir = CobbledOutBreaksForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        Config config = new Config();
        try {
            File file = new File(dir, "config.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledOutBreaksForge.getLog().warn(e);
        }
    }

    public static Config getConfig()
    {
        File dir = CobbledOutBreaksForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "config.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledOutBreaksForge.getLog().error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, Config.class);
    }

    public List<OutBreakLocation> defaultLocations()
    {
        List<OutBreakLocation> locations = new ArrayList<>();
        OutBreakLocation location = new OutBreakLocation();
        location.maxX = 100;
        location.maxY = 255;
        location.maxZ = 100;
        location.minX = 0;
        location.minY = 0;
        location.minZ = 0;
        locations.add(location);
        return locations;
    }
    public void initDefaultLocations()
    {
        File dir = CobbledOutBreaksForge.getStorage();
        dir.mkdirs();

        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        try {
            for (OutBreakLocation location:defaultLocations()) {
                File file = new File(dir, "location_%id%.json".replace("%id%", location.id));
                if (file.exists())
                    continue;
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                String json = gson.toJson(location);
                writer.write(json);
                writer.close();
            }
        } catch (IOException e)
        {
            CobbledOutBreaksForge.getLog().warn(e);
        }

    }
}
