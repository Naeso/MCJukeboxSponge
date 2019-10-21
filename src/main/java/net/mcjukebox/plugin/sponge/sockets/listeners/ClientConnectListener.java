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

public class ClientConnectListener implements Emitter.Listener {

	private JukeboxAPI api;
	private MCJukebox currentInstance;
	private Player user;
	private Cause cause;

	public ClientConnectListener(MCJukebox instance) {
		api = new JukeboxAPI(instance);
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) objects[0];
		user = Sponge.getServer().getPlayer(data.getString("username")).get();
		this.buildCause();
		ClientConnectEvent event = new ClientConnectEvent(
				data.getString("username"), data.getLong("timestamp"), cause);
		Sponge.getEventManager().post(event);

		if(!Sponge.getServer().getPlayer(data.getString("username")).isPresent()) return;
		MessageUtils.sendMessage(user, "event.clientConnect");

		ShowManager showManager = currentInstance.getShowManager();
		if(!showManager.inInShow(user.getUniqueId())) return;

		for(Show show : showManager.getShowsByPlayer(user.getUniqueId())) {
			if(show.getCurrentTrack() != null)
				api.play(user, show.getCurrentTrack());
		}
	}

	private void buildCause(){
		EventContext context = EventContext.builder()
				.add(EventContextKeys.PLAYER, user)
				.build();

		cause = Cause.builder()
				.append(user)
				.build(context);
	}
}
