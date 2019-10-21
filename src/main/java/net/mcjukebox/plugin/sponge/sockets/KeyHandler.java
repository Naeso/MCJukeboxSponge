package net.mcjukebox.plugin.sponge.sockets;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.sockets.api.KeyClass;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class KeyHandler {

	private MCJukebox currentInstance;
	private KeyClass apiKey;
	private DataUtils dataUtils;
	private SocketHandler socketHandler;

	public CommandSource getCurrentlyTryingKey() {
		return currentlyTryingKey;
	}

	private CommandSource currentlyTryingKey;

	public KeyHandler(SocketHandler socketHandler, MCJukebox instance) {
		this.currentInstance = instance;
		this.socketHandler = socketHandler;
		dataUtils = new DataUtils();
	}

	public void onKeyRejected(String reason) {
		if (currentlyTryingKey != null){

			Sponge.getServer().getConsole().sendMessage(
					Text.builder("API key rejected with message: " + reason)
							.color(TextColors.RED)
							.toText());

			if (currentlyTryingKey instanceof Player){
                currentlyTryingKey.sendMessage(
                        Text.builder("API key rejected with message: " + reason)
                                .color(TextColors.RED)
                                .build());
            }

			currentlyTryingKey = null;
		}
	}

	public void tryKey(CommandSource sender, String key) throws IOException {
		currentlyTryingKey = sender;
		apiKey = new KeyClass(key);
		dataUtils.saveObjectToPath(apiKey, currentInstance.getApiKeyPath());
		socketHandler.disconnect();
		socketHandler.attemptConnection();
	}
}
