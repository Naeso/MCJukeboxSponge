package net.mcjukebox.plugin.sponge.utils;

import net.mcjukebox.plugin.sponge.managers.LangManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MessageUtils {

	public static void setLangManager(LangManager langManager) {
		MessageUtils.langManager = langManager;
	}

	private static LangManager langManager;
	private Player player;

	public static void sendMessage(CommandSource src, String message){
		src.sendMessage(Text.of(langManager.get(message)));
	}

	public static void sendURL(Player player, String token) {
		String Url = langManager.get("user.openDomain") + "?token=" + token;

		player.sendMessage(
				Text.builder(langManager.get("user.openClient") + Url)
						.color(TextColors.GOLD)
						.build());
	}
}
