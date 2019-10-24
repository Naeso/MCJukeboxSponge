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

import java.util.Optional;

public class ClientDisconnectListener implements Emitter.Listener {

	private MCJukebox currentInstance;
	private JSONObject data;
	private Optional<Player> playerPresence;
	private Cause cause;

	public ClientDisconnectListener(MCJukebox instance) {
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		data = (JSONObject) objects[0];
		playerPresence = Sponge.getServer().getPlayer(data.getString("username"));
		if (playerPresence.isPresent()) {
			this.buildCause();
			ClientDisconnectEvent event = new ClientDisconnectEvent(
					data.getString("username"), data.getLong("timestamp"), cause);
			Sponge.getEventManager().post(event);

			MessageUtils.sendMessage(playerPresence.get(), "event.clientDisconnect");
		}
	}

	private void buildCause(){
		EventContext context = EventContext.builder()
				.add(EventContextKeys.PLAYER_SIMULATED, playerPresence.get().getProfile())
				.build();

		cause = Cause.builder()
				.append(playerPresence.get())
				.build(context);
	}
}
