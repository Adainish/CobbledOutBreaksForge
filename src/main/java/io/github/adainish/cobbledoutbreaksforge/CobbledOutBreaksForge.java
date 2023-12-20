package io.github.adainish.cobbledoutbreaksforge;

import io.github.adainish.cobbledoutbreaksforge.cmd.Command;
import io.github.adainish.cobbledoutbreaksforge.config.Config;
import io.github.adainish.cobbledoutbreaksforge.listener.EntityListener;
import io.github.adainish.cobbledoutbreaksforge.obj.OutbreaksManager;
import io.github.adainish.cobbledoutbreaksforge.obj.PreviousOutBreaks;
import io.github.adainish.cobbledoutbreaksforge.scheduler.AsyncScheduler;
import io.github.adainish.cobbledoutbreaksforge.tasks.UpdateOutBreaksRunnable;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(CobbledOutBreaksForge.MODID)
public class CobbledOutBreaksForge {

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

    public static PreviousOutBreaks previousOutBreaks;

    public CobbledOutBreaksForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Logger getLog() {
        return log;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobbledOutBreaksForge.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobbledOutBreaksForge.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        CobbledOutBreaksForge.storage = storage;
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        initDirs();
    }

    @SubscribeEvent
    public void onCommandRegistry(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Command.getCommand());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        setServer(ServerLifecycleHooks.getCurrentServer());
        reload();
        MinecraftForge.EVENT_BUS.register(new EntityListener());
    }

    @SubscribeEvent
    public void onServerShutDown(ServerStoppingEvent event)
    {
        if (outbreaksManager != null)
            outbreaksManager.shutdown();
    }

    public void initDirs() {
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/CobbledOutBreaks/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
    }



    public void initConfigs() {
        Config.writeConfig();
        config = Config.getConfig();
        PreviousOutBreaks.writeConfig();
        previousOutBreaks = PreviousOutBreaks.getConfig();
    }

    public void reload() {
        if (!taskList.isEmpty())
        {
            for (AsyncScheduler t:taskList) {
                t.stop();
            }
            taskList.clear();
        }
        if (previousOutBreaks != null) {
            log.info("Saving previous outbreaks");
            previousOutBreaks.save();
        }
        this.initConfigs();
        if (outbreaksManager == null) {
            outbreaksManager = new OutbreaksManager();
        }
        if (previousOutBreaks == null)
            previousOutBreaks = new PreviousOutBreaks();
        outbreaksManager.init();
        this.startTasks();
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
