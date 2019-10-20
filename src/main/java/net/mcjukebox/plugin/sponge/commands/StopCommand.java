package net.mcjukebox.plugin.sponge.commands;

import lombok.Setter;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.json.JSONObject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;

public class StopCommand implements CommandExecutor {

    @Setter
    private static LangManager langManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        JSONObject options = new JSONObject();

        if (args.hasAny("options")) {
            options = jsonFromArgs((String[])args.getAll("options").toArray(), 2);

            if (options == null) {
                src.sendMessage(Text.builder("Unable to parse options as JSON.").color(TextColors.RED).build());
                return CommandResult.empty();
            }
        }

        int fadeDuration = options.has("fadeDuration") ? options.getInt("fadeDuration") : -1;
        String channel = options.has("channel") ? options.getString("channel") : "default";
        String selection = args.<String>getOne("selectionMusicAll").get();
        String targetIndex = args.<String>getOne("userShow").get();

        // Stop music in a show
        if (args.getOne("userShow").get().toString().startsWith("@") && selection.equalsIgnoreCase("all")) {
            JukeboxAPI.getShowManager().getShow(args.<String>getOne("userShow").get()).stopAll(fadeDuration);
            return CommandResult.success();
        }

        // Stop everything in a show
        if (args.getOne("userShow").get().toString().startsWith("@") && selection.equalsIgnoreCase("music")) {
            JukeboxAPI.getShowManager().getShow(args.<String>getOne("userShow").get()).stopMusic(fadeDuration);
            return CommandResult.success();
        }

        // We haven't encountered either show case, so assume a player is the target
        Player target = Sponge.getServer().getPlayer(args.<String>getOne("userShow").get()).get();

        if (target == null) {
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args.<String>getOne("userShow").get());
            src.sendMessage(Text.builder(langManager.get("command.notOnline") + findAndReplace).build());
            return CommandResult.success();
        }

        // Stop music for a particular player
        if (selection.equalsIgnoreCase("all")) {
            JukeboxAPI.stopAll(target, channel, fadeDuration);
            return CommandResult.success();
        }

        // Stop everything for a particular player
        if (selection.equalsIgnoreCase("music")) {
            JukeboxAPI.stopMusic(target, channel, fadeDuration);
            return CommandResult.success();
        }

        return CommandResult.success();
    }

    private JSONObject jsonFromArgs(String[] args, int startPoint) {
        StringBuilder json = new StringBuilder();
        for(int i = startPoint; i < args.length; i++) json.append(args[i]);

        try {
            return new JSONObject(json.toString());
        }catch(Exception ex) {
            return null;
        }
    }
}
