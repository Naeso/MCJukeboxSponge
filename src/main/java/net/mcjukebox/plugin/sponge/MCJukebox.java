package net.mcjukebox.plugin.sponge;

import com.google.inject.Inject;
import net.mcjukebox.plugin.sponge.commands.JukeboxCommandExecutor;
import net.mcjukebox.plugin.sponge.listeners.RegionListener;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.managers.shows.ShowSyncTask;
import net.mcjukebox.plugin.sponge.sockets.SocketHandler;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import net.mcjukebox.plugin.sponge.utils.TimeUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginManager;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "mcjukebox",
        name = "Mcjukebox"
)
public class MCJukebox {

    private static MCJukebox instance;

    public static MCJukebox getInstance() {
        return instance;
    }

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

    private SocketHandler socketHandler;
    private RegionManager regionManager;
    private RegionListener regionListener;
    private LangManager langManager;
    private ShowManager showManager;
    private TimeUtils timeUtils;
    JukeboxCommandExecutor jukeboxCommand;

    @Inject
    public static PluginManager pluginManager;

    @Inject
    @ConfigDir(sharedRoot = false)
    public static Path privateConfigDir;

    @Inject
    public static Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        langManager = new LangManager();
        MessageUtils.setLangManager(langManager);
        socketHandler = new SocketHandler();
        regionManager = new RegionManager();
        showManager = new ShowManager();
        timeUtils = new TimeUtils();

        Sponge.getScheduler().createAsyncExecutor(MCJukebox.getInstance()).schedule(new ShowSyncTask(), 1, TimeUnit.SECONDS);

        if(Sponge.getPluginManager().getPlugin("com.universalguard").isPresent()){
            regionListener = new RegionListener(regionManager);
            Sponge.getEventManager().registerListeners(this, regionListener);
        }

        jukeboxCommand = new JukeboxCommandExecutor(regionManager);

        logger.info("McJukebox for Sponge ready to go.");
    }

    public Path getAPIKey(){
        return DataUtils.loadObjectFromPath(privateConfigDir.resolve("api.key"));
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event){
        logger.info("McJukebox for Sponge is stopping...");
        socketHandler.disconnect();
        regionManager.save();
    }
}
