package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.sockets.SocketHandler;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionListener {
	public ConnectionFailedListener getConnectionFailedListener() {
		return connectionFailedListener;
	}

	public ConnectionSuccessListener getConnectionSuccessListener() {
		return connectionSuccessListener;
	}

	private ConnectionFailedListener connectionFailedListener;
	private ConnectionSuccessListener connectionSuccessListener;

	private SocketHandler socketHandler;
	private HashMap<String, List<JSONObject>> queue = new HashMap<String, List<JSONObject>>();
	private boolean noConnectionWarned = false;

	public ConnectionListener(SocketHandler socketHandler, MCJukebox instance) {
		this.socketHandler = socketHandler;
		connectionFailedListener = new ConnectionFailedListener();
		connectionSuccessListener = new ConnectionSuccessListener(socketHandler.getDropListener(), socketHandler, instance);
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

	public class ConnectionSuccessListener implements Emitter.Listener {
		private MCJukebox instance;

		private DropListener dropListener;
		private SocketHandler socketHandlerSuccess;

		public ConnectionSuccessListener(DropListener dropListener, SocketHandler socketHandler, MCJukebox instance) {
			this.dropListener = dropListener;
			this.socketHandlerSuccess = socketHandler;
			this.instance = instance;
		}

		@Override
		public void call(Object... objects) {
			CommandSource recipient = socketHandlerSuccess.getKeyHandler().getCurrentlyTryingKey();
			String message = "Key accepted and connection to MCJukebox established.";

			if (recipient != null){
				recipient.sendMessage(Text.of(message));
			} else {
				instance.logger.info(message);
			}

			dropListener.setLastDripSent(System.currentTimeMillis());
			socketHandlerSuccess.emit("drip", null);

			for(String channel : queue.keySet()) {
				for(JSONObject params : queue.get(channel)) {
					socketHandlerSuccess.emit(channel, params);
				}
			}

			queue.clear();
			noConnectionWarned = false;
			socketHandlerSuccess.getReconnectTask().reset();
		}

	}

	public void addToQueue(String channel, JSONObject params) {
		List<JSONObject> toRun = queue.containsKey(channel) ? queue.get(channel) : new ArrayList<JSONObject>();
		toRun.add(params);
		queue.put(channel, toRun);
	}
}
