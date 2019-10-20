package net.mcjukebox.plugin.sponge.sockets;

import lombok.Getter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;;

public class KeyHandler {

	private SocketHandler socketHandler;
	@Getter private CommandSource currentlyTryingKey;

	public KeyHandler(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	public void onKeyRejected(String reason) {
		if (currentlyTryingKey != null){
			Sponge.getServer().getConsole().sendMessage(
					Text.builder("API key rejected with message: " + reason)
							.color(TextColors.RED)
							.toText());
			currentlyTryingKey = null;
		}
	}

	public void tryKey(CommandSource sender, String key) {
		currentlyTryingKey = sender;
		DataUtils.saveObjectToPath(key, MCJukebox.getInstance().getAPIKey());
		socketHandler.disconnect();
		socketHandler.attemptConnection();
	}
}
