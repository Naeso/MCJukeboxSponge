package net.mcjukebox.plugin.sponge.events;

import lombok.Getter;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class ClientConnectEvent extends AbstractEvent {

	@Getter private String username;
	@Getter private long timestamp;
	private Cause cause;

	public ClientConnectEvent(String username, long timestamp, Cause cause) {
		this.username = username;
		this.timestamp = timestamp;
		this.cause = cause;
	}

	@Override
	public Cause getCause() {
		return cause;
	}
}
