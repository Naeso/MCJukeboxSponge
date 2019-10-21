package net.mcjukebox.plugin.sponge.managers.shows;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import org.json.JSONException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class Show {

	private MCJukebox currentInstance;
	private JukeboxAPI api;

	public Show(MCJukebox instance) {
		this.currentInstance = instance;
		api = new JukeboxAPI(instance);
	}

	public HashMap<UUID, Boolean> getMembers() {
		return members;
	}

	public Media getCurrentTrack() {
		return currentTrack;
	}

	private HashMap<UUID, Boolean> members = new HashMap<UUID, Boolean>();
	private Media currentTrack;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	private String channel = "default";

	public void addMember(Player player, boolean addedByRegion) throws JSONException {
		if(members.containsKey(player.getUniqueId())) return;
		members.put(player.getUniqueId(), addedByRegion);

		if(currentTrack != null) api.play(player, currentTrack);
	}

	public void removeMember(Player player) throws JSONException {
		if(!members.containsKey(player.getUniqueId())) return;
		members.remove(player.getUniqueId());
		api.stopAll(player, channel, -1);
	}

	public void play(Media media) throws JSONException {
		media.setStartTime(currentInstance.getTimeUtils().currentTimeMillis());
		media.setChannel(channel);

		if(media.getType() == ResourceType.MUSIC) {
			currentTrack = media;
		}

		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			api.play(Sponge.getGame().getServer().getPlayer(UUID).get(), media);
		}
	}

	public void stopMusic() throws JSONException {
		stopMusic(-1);
	}

	public void stopMusic(int fadeDuration) throws JSONException {
		if(currentTrack == null) return;
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			api.stopMusic(Sponge.getGame().getServer().getPlayer(UUID).get(), channel, fadeDuration);
		}
		currentTrack = null;
	}

	public void stopAll() throws JSONException {
		stopAll(-1);
	}

	public void stopAll(int fadeDuration) throws JSONException {
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			api.stopAll(Sponge.getGame().getServer().getPlayer(UUID).get(), channel, fadeDuration);
		}
		this.currentTrack = null;
	}

	protected void jumpBack(long offset) throws JSONException {
		if (currentTrack == null) return;
		currentTrack.setStartTime(currentTrack.getStartTime() + offset);

		// Send updated start time to all connected players
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			api.play(Sponge.getGame().getServer().getPlayer(UUID).get(), currentTrack);
		}
	}

}
