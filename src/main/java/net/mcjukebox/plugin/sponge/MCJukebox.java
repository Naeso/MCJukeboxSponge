package net.mcjukebox.plugin.sponge;

import com.google.inject.Inject;
import net.mcjukebox.plugin.sponge.commands.JukeboxCommandExecutor;
import net.mcjukebox.plugin.sponge.listeners.RegionListener;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.managers.shows.ShowSyncTask;
import net.mcjukebox.plugin.sponge.sockets.SocketHandler;
import net.mcjukebox.plugin.sponge.sockets.api.KeyClass;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import net.mcjukebox.plugin.sponge.utils.TimeUtils;
import net.mcjukebox.shared.utils.DatabaseUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "mcjukebox",
        name = "Mcjukebox",
        description = "play custom music!"
)
public class MCJukebox {

    public SocketHandler getSocketHandler() {
        return socketHandler;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionListener getRegionListener() {
        return regionListener;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public ShowManager getShowManager() {
        return showManager;
    }

    public TimeUtils getTimeUtils() {
        return timeUtils;
    }

    public Path getPrivateConfigDir() {
        return privateConfigDir;
    }

    public Logger getLogger() {
        return logger;
    }

    private SocketHandler socketHandler;
    private RegionManager regionManager;
    private RegionListener regionListener;
    private LangManager langManager;
    private ShowManager showManager;
    private TimeUtils timeUtils;
    private Task.Builder taskBuilder = Task.builder();
    private DataUtils dataUtils;
    private DatabaseUtils databseUtils;

    @Inject
    public PluginManager pluginManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path privateConfigDir;

    @Inject
    public Logger logger;

    @Inject
    private PluginContainer pluginContainer;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        langManager = new LangManager();
        MessageUtils.setLangManager(langManager);
        socketHandler = new SocketHandler(this);
        regionManager = new RegionManager(this);
        showManager = new ShowManager(this);
        timeUtils = new TimeUtils(this);
        databseUtils = new DatabaseUtils();

        databseUtils.createDatabase();

        taskBuilder.async().execute(new ShowSyncTask()).delay(1, TimeUnit.SECONDS).submit(this);

        if(Sponge.getPluginManager().getPlugin("universeguard").isPresent()){
            regionListener = new RegionListener(regionManager, this);
            Sponge.getEventManager().registerListeners(this, regionListener);
            logger.info("Universe Guard detected.");
        }else{
            logger.info("Universe Guard isn't present");
        }

        JukeboxCommandExecutor jukeboxCommand = new JukeboxCommandExecutor(regionManager, this);

        logger.info("McJukebox for Sponge ready to go.");
    }


    @Listener
    public void onServerStop(GameStoppedServerEvent event){
        logger.info("McJukebox for Sponge is stopping...");
        socketHandler.disconnect();
    }

    public String getAPIKey(){
        dataUtils = new DataUtils();
        if (Files.exists(privateConfigDir.resolve("api.key"))){
            return dataUtils.loadAPIKey(privateConfigDir.resolve("api.key"));
        }
        else{
            logger.info("Api.key file does not exists ! Creating one...");
            dataUtils.saveObjectToPath(new KeyClass(""), privateConfigDir.resolve("api.key"));
            return "";
        }
    }

    public Path getApiKeyPath(){
        return privateConfigDir.resolve("api.key");
    }

    public boolean doesUniverseGuardIsPresent(){
        return this.pluginManager.getPlugin("universeguard").isPresent();
    }
}
