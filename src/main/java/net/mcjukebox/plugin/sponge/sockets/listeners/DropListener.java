package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;

public class DropListener implements Emitter.Listener {

	private MCJukebox currentInstance;

	public DropListener(MCJukebox instance) {
		this.currentInstance = instance;
	}

	public void setLastDripSent(long lastDripSent) {
		this.lastDripSent = lastDripSent;
	}

	private long lastDripSent;

	@Override
	public void call(Object... objects) {
		long roundTripTime = System.currentTimeMillis() - lastDripSent;
		long serverTime = (long) objects[0];
		currentInstance.getTimeUtils().updateOffset(roundTripTime, serverTime);
	}
}
