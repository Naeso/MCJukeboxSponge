package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.sockets.SocketHandler;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionListener {

	@Getter private ConnectionFailedListener connectionFailedListener;
	@Getter private ConnectionSuccessListener connectionSuccessListener;

	private SocketHandler socketHandler;
	private HashMap<String, List<JSONObject>> queue = new HashMap<String, List<JSONObject>>();
	private boolean noConnectionWarned = false;

	public ConnectionListener(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
		connectionFailedListener = new ConnectionFailedListener();
		connectionSuccessListener = new ConnectionSuccessListener(socketHandler.getDropListener());
	}

	public class ConnectionFailedListener implements Emitter.Listener {

		@Override
		public void call(Object... objects) {
			String reason = objects.length > 0 && objects[0] instanceof String ? (String) objects[0] : null;

			if(reason != null) {
				socketHandler.getKeyHandler().onKeyRejected(reason);
				return;
			}

			socketHandler.getReconnectTask().setReconnecting(false);
			if(noConnectionWarned) return;

			CommandSource recipient = socketHandler.getKeyHandler().getCurrentlyTryingKey();
			if(recipient == null) recipient = Sponge.getServer().getConsole();
			recipient.sendMessage(Text.builder("Unable to connect to MCJukebox.").color(TextColors.RED).build());
			recipient.sendMessage(Text.builder("This could be caused by a period of server maintenance.").color(TextColors.RED).build());
			recipient.sendMessage(Text.builder("If the problem persists, please email support@mcjukebox.net").color(TextColors.RED).build());
			noConnectionWarned = true;
		}

	}

	@AllArgsConstructor
	public class ConnectionSuccessListener implements Emitter.Listener {

		private DropListener dropListener;

		@Override
		public void call(Object... objects) {
			CommandSource recipient = socketHandler.getKeyHandler().getCurrentlyTryingKey();
			String message = "Key accepted and connection to MCJukebox established.";

			if (recipient != null){
				recipient.sendMessage(Text.builder(message).build());
			} else {
				MCJukebox.logger.info(message);
			}

			dropListener.setLastDripSent(System.currentTimeMillis());
			socketHandler.emit("drip", null);

			for(String channel : queue.keySet()) {
				for(JSONObject params : queue.get(channel)) {
					socketHandler.emit(channel, params);
				}
			}

			queue.clear();
			noConnectionWarned = false;
			socketHandler.getReconnectTask().reset();
		}

	}

	public void addToQueue(String channel, JSONObject params) {
		List<JSONObject> toRun = queue.containsKey(channel) ? queue.get(channel) : new ArrayList<JSONObject>();
		toRun.add(params);
		queue.put(channel, toRun);
	}

}
