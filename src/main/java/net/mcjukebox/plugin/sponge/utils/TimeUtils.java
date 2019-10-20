package net.mcjukebox.plugin.sponge.utils;

import net.mcjukebox.plugin.sponge.MCJukebox;

public class TimeUtils {

	private static boolean hasUpdated = false;
	private static long offset = 0;

	public boolean hasUpdated() {
		return hasUpdated;
	}

	public void updateOffset(long roundTripTime, long serverTime) {
		long offsetFromServer = roundTripTime / 2;
		long estimatedTime = serverTime + offsetFromServer;

		offset = estimatedTime - System.currentTimeMillis();

		if (!hasUpdated()) {
			// This is our first offset prediction
			MCJukebox.logger.info("Predicted time offset: " + offset + "ms.");
			MCJukebox.logger.info("This will be silently updated every 30 seconds.");
			hasUpdated = true;
		}
	}

	public long currentTimeMillis() {
		return System.currentTimeMillis() + offset;
	}

}
