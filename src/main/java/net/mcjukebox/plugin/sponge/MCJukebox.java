package net.mcjukebox.plugin.sponge;

import com.google.inject.Inject;
import lombok.Getter;
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
        name = "Mcjukebox",
        description = "Here lies an example plugin definition"
)
public class MCJukebox {


    @Getter private static MCJukebox instance;
    @Getter private SocketHandler socketHandler;
    @Getter private RegionManager regionManager;
    @Getter private RegionListener regionListener;
    @Getter private LangManager langManager;
    @Getter private ShowManager showManager;
    @Getter private TimeUtils timeUtils;
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
