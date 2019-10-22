package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;

public class StopCommand implements CommandExecutor {

    public static void setLangManager(LangManager langManager) {
        StopCommand.langManager = langManager;
    }
    private JukeboxAPI api;
    private static LangManager langManager;

    private MCJukebox currentInstance;

    public StopCommand(MCJukebox instance) {
        this.currentInstance = instance;
        api = new JukeboxAPI(currentInstance);
    }

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
        String selection = args.<String>getOne("MusicOrAll").get();
        String targetIndex = args.<String>getOne("UserOrShow").get();

        // Stop music in a show
        if (args.getOne("UserOrShow").get().toString().startsWith("@") &&
                args.getOne("MusicOrAll").get().toString().equalsIgnoreCase("all")) {
            api.getShowManager().getShow(args.<String>getOne("UserOrShow").get()).stopAll(fadeDuration);
            return CommandResult.success();
        }

        // Stop everything in a show
        if (args.getOne("UserOrShow").get().toString().startsWith("@") &&
                args.getOne("MusicOrAll").get().toString().equalsIgnoreCase("music")) {
            api.getShowManager().getShow(args.<String>getOne("UserOrShow").get()).stopMusic(fadeDuration);
            return CommandResult.success();
        }

        // We haven't encountered either show case, so assume a player is the target
        Player target = Sponge.getServer().getPlayer(args.<String>getOne("UserOrShow").get()).get();

        if (!target.isOnline()) {
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", args.<String>getOne("UserOrShow").get());
            src.sendMessage(Text.builder(langManager.get("command.notOnline") + findAndReplace).build());
            return CommandResult.empty();
        }

        // Stop music for a particular player
        if (args.getOne("MusicOrAll").get().toString().equalsIgnoreCase("all")) {
            api.stopAll(target, channel, fadeDuration);
            return CommandResult.success();
        }

        // Stop everything for a particular player
        if (args.getOne("MusicOrAll").get().toString().equalsIgnoreCase("music")) {
            api.stopMusic(target, channel, fadeDuration);
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
