package net.mcjukebox.plugin.sponge.utils;

import net.mcjukebox.plugin.sponge.managers.LangManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MessageUtils {

	public static void setLangManager(LangManager langManager) {
		MessageUtils.langManager = langManager;
	}

	private static LangManager langManager;
	private Player player;

	public static void sendMessage(CommandSource player, String key){
		sendMessage(player, key, null);
	}

	public static void sendMessage(CommandSource player, String key, HashMap<String, String> findAndReplace){
		String message = langManager.get(key);

		//Don't send message if the localisation is blank
		if(message.trim().equalsIgnoreCase("")) return;

		//Replace any values in the find and replace HashMap, if it is present
		if (findAndReplace != null) {
			for (String find : findAndReplace.keySet()) message = message.replace("[" + find + "]", findAndReplace.get(find));
		}

		player.sendMessage(Text.of(message));
	}

	public static void sendURL(Player player, String token) {
		String Url = langManager.get("user.openDomain") + "?token=" + token;

		try {
			player.sendMessage(
					Text.builder(langManager.get("user.openClient"))
							.color(TextColors.GOLD).append(Text.of(Url)).onClick(TextActions.openUrl(new URL(Url)))
							.build());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
