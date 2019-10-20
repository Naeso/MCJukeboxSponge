package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.events.ClientDisconnectEvent;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;

public class ClientDisconnectListener implements Emitter.Listener {

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) objects[0];
		ClientDisconnectEvent event = new ClientDisconnectEvent(
				data.getString("username"), data.getLong("timestamp"), Sponge.getCauseStackManager().getCurrentCause());
		Sponge.getEventManager().post(event);

		if(!Sponge.getGame().getServer().getPlayer(data.getString("username")).isPresent()) return;
		MessageUtils.sendMessage(Sponge.getGame().getServer().getPlayer(data.getString("username")).get(), "event.clientDisconnect");
	}

}
