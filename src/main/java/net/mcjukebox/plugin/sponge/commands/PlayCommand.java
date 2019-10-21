package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
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

import java.util.HashMap;

public class PlayCommand implements CommandExecutor {

    private JukeboxAPI api;

    private MCJukebox currentInstance;

    private ResourceType type;

    public PlayCommand(MCJukebox instance, ResourceType type) {
        this.currentInstance = instance;
        api = new JukeboxAPI(instance);
        this.type = type;
    }

    public static void setLangManager(LangManager langManager) {
        PlayCommand.langManager = langManager;
    }

    private static LangManager langManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String url = args.<String>getOne("URL").get();
        Media toPlay = new Media(type, url, currentInstance);

        if (args.hasAny("options")) {
            JSONObject options = jsonFromArgs((String[])args.getAll("options").toArray(), 2);

            if (options == null) {
                src.sendMessage(Text.builder("Unable to parse options as JSON.").color(TextColors.RED).build());
                return CommandResult.empty();
            }

            try {
                toPlay.loadOptions(options);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (args.<String>getOne("UserOrShow").get().startsWith("@")) {
            try {
                api.getShowManager().getShow(args.getOne("UserOrShow").get().toString()).play(toPlay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Player player = Sponge.getServer().getPlayer(args.<String>getOne("UserOrShow").get()).get();
            if (player.isOnline()) {
                try {
                    api.play(player, toPlay);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                HashMap<String, String> findAndReplace = new HashMap<String, String>();
                findAndReplace.put("user", args.<String>getOne("UserOrShow").get());
                try {
                    src.sendMessage(Text.builder(langManager.get("command.notOnline") + findAndReplace).build());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
