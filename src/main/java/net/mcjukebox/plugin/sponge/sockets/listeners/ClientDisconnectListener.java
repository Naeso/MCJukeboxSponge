package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.events.ClientDisconnectEvent;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;

public class ClientDisconnectListener implements Emitter.Listener {

	private MCJukebox currentInstance;
	private JSONObject data;
	private Player user;
	private Cause cause;

	public ClientDisconnectListener(MCJukebox instance) {
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		data = (JSONObject) objects[0];
		user = Sponge.getGame().getServer().getPlayer(data.getString("username")).get();
		this.buildCause();
		ClientDisconnectEvent event = new ClientDisconnectEvent(
				data.getString("username"), data.getLong("timestamp"), cause);
		Sponge.getEventManager().post(event);

		if(!Sponge.getGame().getServer().getPlayer(data.getString("username")).isPresent()) return;
		MessageUtils.sendMessage(user, "event.clientDisconnect");
	}

	private void buildCause(){
		EventContext context = EventContext.builder()
				.add(EventContextKeys.PLAYER_SIMULATED, user.getProfile())
				.build();

		cause = Cause.builder()
				.append(user)
				.build(context);
	}
}
