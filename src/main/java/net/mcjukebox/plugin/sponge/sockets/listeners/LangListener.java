package net.mcjukebox.plugin.sponge.sockets.listeners;

import io.socket.emitter.Emitter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import org.json.JSONObject;

public class LangListener implements Emitter.Listener {

	private MCJukebox currentInstance;

	public LangListener(MCJukebox instance) {
		this.currentInstance = instance;
	}

	@Override
	public void call(Object... objects) {
		JSONObject data = (JSONObject) (objects.length == 2 ? objects[1] : objects[0]);
		currentInstance.getLangManager().loadLang(data);
	}

}
