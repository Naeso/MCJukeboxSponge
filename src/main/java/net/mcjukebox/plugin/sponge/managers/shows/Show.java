package net.mcjukebox.plugin.sponge.managers.shows;

import lombok.Getter;
import lombok.Setter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class Show {

	@Getter private HashMap<UUID, Boolean> members = new HashMap<UUID, Boolean>();
	@Getter private Media currentTrack;
	@Getter @Setter private String channel = "default";

	public void addMember(Player player, boolean addedByRegion) {
		if(members.containsKey(player.getUniqueId())) return;
		members.put(player.getUniqueId(), addedByRegion);

		if(currentTrack != null) JukeboxAPI.play(player, currentTrack);
	}

	public void removeMember(Player player) {
		if(!members.containsKey(player.getUniqueId())) return;
		members.remove(player.getUniqueId());
		JukeboxAPI.stopAll(player, channel, -1);
	}

	public void play(Media media) {
		media.setStartTime(MCJukebox.getInstance().getTimeUtils().currentTimeMillis());
		media.setChannel(channel);

		if(media.getType() == ResourceType.MUSIC) {
			currentTrack = media;
		}

		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			JukeboxAPI.play(Sponge.getGame().getServer().getPlayer(UUID).get(), media);
		}
	}

	public void stopMusic() {
		stopMusic(-1);
	}

	public void stopMusic(int fadeDuration) {
		if(currentTrack == null) return;
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			JukeboxAPI.stopMusic(Sponge.getGame().getServer().getPlayer(UUID).get(), channel, fadeDuration);
		}
		currentTrack = null;
	}

	public void stopAll() {
		stopAll(-1);
	}

	public void stopAll(int fadeDuration) {
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			JukeboxAPI.stopAll(Sponge.getGame().getServer().getPlayer(UUID).get(), channel, fadeDuration);
		}
		this.currentTrack = null;
	}

	protected void jumpBack(long offset) {
		if (currentTrack == null) return;
		currentTrack.setStartTime(currentTrack.getStartTime() + offset);

		// Send updated start time to all connected players
		for(UUID UUID : members.keySet()) {
			if(!Sponge.getGame().getServer().getPlayer(UUID).isPresent()) continue;
			JukeboxAPI.play(Sponge.getGame().getServer().getPlayer(UUID).get(), currentTrack);
		}
	}

}
