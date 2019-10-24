package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.events.ClientConnectEvent;
import net.mcjukebox.plugin.sponge.managers.shows.Show;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;

import java.util.Optional;

public class ClientConnectListener implements Emitter.Listener {

	private JukeboxAPI api;
	private MCJukebox currentInstance;
	private Cause cause;
	private Optional<Player> playerPresence;

	public ClientConnectListener(MCJukebox instance) {
		api = new JukeboxAPI(instance);
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) objects[0];
		playerPresence = Sponge.getServer().getPlayer(data.getString("username"));
		if (playerPresence.isPresent()) {
			this.buildCause();
			ClientConnectEvent event = new ClientConnectEvent(
					data.getString("username"), data.getLong("timestamp"), cause);
			Sponge.getEventManager().post(event);

			MessageUtils.sendMessage(playerPresence.get(), "event.clientConnect");

			ShowManager showManager = currentInstance.getShowManager();
			if(!showManager.inInShow(playerPresence.get().getUniqueId())) return;

			for(Show show : showManager.getShowsByPlayer(playerPresence.get().getUniqueId())) {
				if(show.getCurrentTrack() != null)
					api.play(playerPresence.get(), show.getCurrentTrack());
			}
		}
	}

	private void buildCause(){
		EventContext context = EventContext.builder()
				.add(EventContextKeys.PLAYER, playerPresence.get())
				.build();

		cause = Cause.builder()
				.append(playerPresence.get())
				.build(context);
	}
}
