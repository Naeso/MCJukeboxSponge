package net.mcjukebox.plugin.sponge.sockets;

import io.socket.client.IO;
import io.socket.client.Socket;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.sockets.listeners.*;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public class SocketHandler {
	private MCJukebox currentInstance;

	private Socket server;
	private ReconnectTask reconnectTask;
	private DripTask dripTask;
	private DropListener dropListener;
	private TokenListener tokenListener;
	private KeyHandler keyHandler;

	public Socket getServer() {
		return server;
	}

	public ReconnectTask getReconnectTask() {
		return reconnectTask;
	}

	public DripTask getDripTask() {
		return dripTask;
	}

	public DropListener getDropListener() {
		return dropListener;
	}

	public TokenListener getTokenListener() {
		return tokenListener;
	}

	public KeyHandler getKeyHandler() {
		return keyHandler;
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	private ConnectionListener connectionListener;

	public SocketHandler(MCJukebox instance) {
		this.currentInstance = instance;
		dropListener = new DropListener(instance);
		keyHandler = new KeyHandler(this, instance);
		reconnectTask = new ReconnectTask(this);
		dripTask = new DripTask(this);
		tokenListener = new TokenListener();
		connectionListener = new ConnectionListener(this, currentInstance);

		Task.Builder taskBuilder = Task.builder();
		taskBuilder.async().execute(reconnectTask).delay(30, TimeUnit.SECONDS).submit(instance);
		taskBuilder.async().execute(dripTask).delay(30, TimeUnit.SECONDS).submit(instance);

		attemptConnection();
	}

	public void attemptConnection() {
		try {
			if(currentInstance.getAPIKey().isEmpty()) {
				currentInstance.logger.error("No API key set - ignoring attempt to connect.");
				return;
			}

			IO.Options opts = new IO.Options();
			opts.secure = true;
			opts.reconnection = false;
			opts.query = "APIKey=" + currentInstance.getAPIKey();

			String url = "https://secure.ws.mcjukebox.net";

			server = IO.socket(url, opts);
			server.connect();

			registerEventListeners();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void registerEventListeners() {
		//Connection issue listener
		server.on(Socket.EVENT_ERROR, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT_ERROR, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT_TIMEOUT, connectionListener.getConnectionFailedListener());
		server.on(Socket.EVENT_CONNECT, connectionListener.getConnectionSuccessListener());

		//Event and data listeners
		server.on("drop", dropListener);
		server.on("event/clientConnect", new ClientConnectListener(currentInstance));
		server.on("event/clientDisconnect", new ClientDisconnectListener(currentInstance));
		server.on("data/lang", new LangListener(currentInstance));
		server.on("data/token", tokenListener);
	}

	public void emit(String channel, JSONObject params) {
		if(server == null || !server.connected()) connectionListener.addToQueue(channel, params);
		else server.emit(channel, params);
	}

	public void disconnect() {
		if(server == null || !server.connected()) return;
		server.close();
	}

}
