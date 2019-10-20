package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;

public class DropListener implements Emitter.Listener {

	public void setLastDripSent(long lastDripSent) {
		this.lastDripSent = lastDripSent;
	}

	private long lastDripSent;

	@Override
	public void call(Object... objects) {
		long roundTripTime = System.currentTimeMillis() - lastDripSent;
		long serverTime = (long) objects[0];
		MCJukebox.getInstance().getTimeUtils().updateOffset(roundTripTime, serverTime);
	}

}
