package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import org.json.JSONException;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class StopCommand implements CommandExecutor {

    private JukeboxAPI api;
    private MCJukebox currentInstance;
    private LangManager langManager;

    private String selectionMusicOrAll;

    private Collection<Player> ListOfPlayerSelected;
    private String targetShow;

    private int fadeDuration;
    private String channel;
    private JSONObject options;

    public StopCommand(MCJukebox instance) {
        this.currentInstance = instance;
        langManager = new LangManager();
        api = new JukeboxAPI(currentInstance);
        options = new JSONObject();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(args.hasAny("Show")){
            targetShow = args.requireOne("Show");
        }else if (args.hasAny("User")){
            ListOfPlayerSelected = args.getAll("User");
        }

        selectionMusicOrAll = args.<String>getOne("MusicOrAll").orElse("music");

        if (args.hasAny("options")) {
            options = jsonFromArgs((String[])args.getAll("options").toArray(), 2);
            if (options == null) {
                src.sendMessage(Text.builder("Unable to parse options as JSON.").color(TextColors.RED).build());
                return CommandResult.empty();
            }
        }

        fadeDuration = options.has("fadeDuration") ? options.getInt("fadeDuration") : -1;
        channel = options.has("channel") ? options.getString("channel") : "default";

        if(args.hasAny("Show")){
            // Stop everything in a show
            if (selectionMusicOrAll.equalsIgnoreCase("all")) {
                api.getShowManager().getShow(targetShow).stopAll(fadeDuration);
                return CommandResult.success();
            }

            // Stop music in a show
            if (selectionMusicOrAll.equalsIgnoreCase("music")) {
                api.getShowManager().getShow(targetShow).stopMusic(fadeDuration);
                return CommandResult.success();
            }
        }

        if (args.hasAny("User")) {
            for (Player user : ListOfPlayerSelected) {
                if (selectionMusicOrAll.equalsIgnoreCase("all")) {
                    api.stopAll(user, channel, fadeDuration);
                }

                if (selectionMusicOrAll.equalsIgnoreCase("music")) {
                    api.stopMusic(user, channel, fadeDuration);
                }
            }
            return CommandResult.success();
        }
        return CommandResult.empty();
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
