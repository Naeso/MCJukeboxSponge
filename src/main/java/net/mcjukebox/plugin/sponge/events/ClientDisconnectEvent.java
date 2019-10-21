package net.mcjukebox.plugin.sponge.events;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class ClientDisconnectEvent extends AbstractEvent {

	public String getUsername() {
		return username;
	}

	public long getTimestamp() {
		return timestamp;
	}

	private String username;
	private long timestamp;
	private Cause cause;

	public ClientDisconnectEvent(String username, long timestamp, Cause cause) {
		this.username = username;
		this.timestamp = timestamp;
		this.cause = cause;
	}

	@Override
	public Cause getCause() {
		return cause;
	}
}
