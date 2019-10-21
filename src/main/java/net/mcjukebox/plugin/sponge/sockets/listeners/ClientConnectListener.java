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

public class ClientConnectListener implements Emitter.Listener {

	private JukeboxAPI api;
	private MCJukebox currentInstance;

	public ClientConnectListener(MCJukebox instance) {
		api = new JukeboxAPI(instance);
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) objects[0];
		ClientConnectEvent event = new ClientConnectEvent(
				data.getString("username"), data.getLong("timestamp"), Sponge.getCauseStackManager().getCurrentCause());
		Sponge.getEventManager().post(event);

		if(Sponge.getServer().getPlayer(data.getString("username")).isPresent()) return;
		Player player = Sponge.getServer().getPlayer(data.getString("username")).get();
		MessageUtils.sendMessage(player, "event.clientConnect");

		ShowManager showManager = currentInstance.getShowManager();
		if(!showManager.inInShow(player.getUniqueId())) return;

		for(Show show : showManager.getShowsByPlayer(player.getUniqueId())) {
			if(show.getCurrentTrack() != null)
				api.play(player, show.getCurrentTrack());
		}
	}

}
