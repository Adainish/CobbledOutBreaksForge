package io.github.adainish.cobbledoutbreaksforge;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.api.scheduling.ScheduledTaskTracker;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.cobbledoutbreaksforge.config.Config;
import io.github.adainish.cobbledoutbreaksforge.listener.EntityListener;
import io.github.adainish.cobbledoutbreaksforge.obj.OutbreaksManager;
import io.github.adainish.cobbledoutbreaksforge.scheduler.AsyncScheduler;
import io.github.adainish.cobbledoutbreaksforge.tasks.UpdateOutBreaksRunnable;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
public class CobbledOutBreaks implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobbledoutbreaksforge";

    public static final String MOD_NAME = "CobbledOutbreaks";
    public static final String VERSION = "1.0.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";
    private static final Logger log = LogManager.getLogger(MOD_NAME);
    private static MinecraftServer server;
    private static File configDir;
    private static File storage;

    public static OutbreaksManager outbreaksManager;

    public static Config config;

    public List<AsyncScheduler> taskList = new ArrayList<>();

    public EntityListener entityListener;


    @Override
    public void onInitialize()
    {
        this.commonSetup();

        PlatformEvents.SERVER_STARTED.subscribe(Priority.NORMAL, event -> {
            server = event.getServer();
            reload();
            this.entityListener = new EntityListener();
            return Unit.INSTANCE;
        });

        PlatformEvents.SERVER_STOPPING.subscribe(Priority.NORMAL, event -> {
            if (outbreaksManager != null)
                outbreaksManager.shutdown();
            return Unit.INSTANCE;
        });
    }

    public static Logger getLog() {
        return log;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobbledOutBreaks.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobbledOutBreaks.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        CobbledOutBreaks.storage = storage;
    }


    private void commonSetup() {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        initDirs();
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
            if (outbreaksManager != null)
                if (!event.getPokemon().getPersistentData().isEmpty() && event.getPokemon().getPersistentData().getBoolean("outbreakmon"))
                    event.getPokemon().getPersistentData().putBoolean("outbreakmon", false);
            return Unit.INSTANCE;
        });
    }

    public void initDirs() {
        setConfigDir(new File(FabricLoader.getInstance().getConfigDir() + "/CobbledOutBreaks/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
    }



    public void initConfigs() {
        Config.writeConfig();
        config = Config.getConfig();
    }

    public void reload() {
        if (!taskList.isEmpty())
        {
            for (AsyncScheduler t:taskList) {
                t.stop();
            }
            taskList.clear();
        }
        initConfigs();
        if (outbreaksManager == null) {
            outbreaksManager = new OutbreaksManager();
        }
        outbreaksManager.init();
        startTasks();
    }

    public void startTasks()
    {

        AsyncScheduler.Builder builder = new AsyncScheduler.Builder();
        AsyncScheduler updateOutBreaksRunnableTask = builder.withInfiniteIterations().withInterval((20 * 60))
                .withRunnable(new UpdateOutBreaksRunnable())
                .build();
        updateOutBreaksRunnableTask.start();
    }


}
