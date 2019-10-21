package net.mcjukebox.plugin.sponge.api;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.sockets.listeners.TokenListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.TimeUnit;

public class JukeboxAPI {

    private MCJukebox currentInstance;

    public JukeboxAPI(MCJukebox instance) {
        this.currentInstance = instance;
    }

    /**
     * Requests that an audio file is played to the player.
     *
     * @param player The player for which the song should be played
     * @param media The file which should be played
     */
    public void play(Player player, final Media media) throws JSONException {
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("url", media.getURL());
        params.put("volume", media.getVolume());
        params.put("looping", media.isLooping());
        params.put("channel", media.getChannel());
        if(media.getFadeDuration() != -1) params.put("fadeDuration", media.getFadeDuration());
        if(media.getStartTime() != -1) params.put("startTime", media.getStartTime());
        Sponge.getScheduler().createAsyncExecutor(currentInstance).schedule(new Runnable() {
            @Override
            public void run() {
                String channel = media.getType() == ResourceType.MUSIC ? "playMusic" : "playSound";
                currentInstance.getSocketHandler().emit("command/" + channel, params);
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Stops the current music track.
     *
     * @param player The player to stop the music for.
     */
    public void stopMusic(Player player) throws JSONException {
        stopMusic(player, "default", -1);
    }

    /**
     * Stops the current music track.
     *
     * @param player The player to stop the music for.
     * @param channel Channel to play music in, default is "default"
     * @param fadeDuration Length of fade, use 0 to disable and -1 for default
     */
    public void stopMusic(Player player, String channel, int fadeDuration) throws JSONException {
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("channel", channel);
        if(fadeDuration != -1) params.put("fadeDuration", fadeDuration);
        Sponge.getScheduler().createAsyncExecutor(currentInstance).schedule( new Runnable() {
            @Override
            public void run() {
                currentInstance.getSocketHandler().emit("command/stopMusic", params);
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Stops all music and sounds in a channel.
     *
     * @param player The player to stop the music for.
     * @param channel Channel to play music in, default is "default"
     * @param fadeDuration Length of fade, use 0 to disable and -1 for default
     */
    public void stopAll(Player player, String channel, int fadeDuration) throws JSONException {
        final JSONObject params = new JSONObject();
        params.put("username", player.getName());
        params.put("channel", channel);
        if(fadeDuration != -1) params.put("fadeDuration", fadeDuration);
;       Sponge.getScheduler().createAsyncExecutor(currentInstance).schedule( new Runnable() {
            public void run() {
                currentInstance.getSocketHandler().emit("command/stopAll", params);
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Gets the authentication token needed for a player to open the client. Since tokens are requested from the server,
     * this method blocks the thread until a token is received. It should always be run asynchronously.
     *
     * @param player The player to get the token for
     * @return Token to be used in the query parameters of the client URL
     */
    public String getToken(Player player) throws JSONException {
        // Construct and send request for token
        JSONObject params = new JSONObject();
        params.put("username", player.getName());
        currentInstance.getSocketHandler().emit("command/getToken", params);

        // Create a new lock that can be notified when a token is received
        Object lock = new Object();

        synchronized (lock) {
            try {
                TokenListener tokenListener = currentInstance.getSocketHandler().getTokenListener();
                // Register the lock so that we are notified when a new token is received
                tokenListener.addLock(player.getName(), lock);
                lock.wait();
                return tokenListener.getToken(player.getName());
            } catch (InterruptedException exception) {
                // This should never happen, so someone has gone out of their way to cancel this call
                return null;
            }
        }
    }

	/**
	 * Gets the ShowManager instance currently in use. Note that show are a client side feature, implemented
     * as as a higher level version of our internal channels system which allows multiple audio tracks
     * to be played simultaneously.
     *
     * @return ShowManager being used by MCJukebox
     */
    public ShowManager getShowManager() {
        return currentInstance.getShowManager();
    }

}
