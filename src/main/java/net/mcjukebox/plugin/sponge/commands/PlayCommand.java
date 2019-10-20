package net.mcjukebox.plugin.sponge.commands;

import lombok.AllArgsConstructor;
import lombok.Setter;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
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

@AllArgsConstructor
public class PlayCommand implements CommandExecutor {

    private ResourceType type;

    @Setter
    private static LangManager langManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String url = args.getOne("URL").get().toString();
        Media toPlay = new Media(type, url);

        if (args.hasAny("options")) {
            JSONObject options = jsonFromArgs((String[])args.getAll("options").toArray(), 2);

            if (options == null) {
                src.sendMessage(Text.builder("Unable to parse options as JSON.").color(TextColors.RED).build());
                return CommandResult.empty();
            }

            toPlay.loadOptions(options);
        }

        if (args.getOne("userShow").get().toString().startsWith("@")) {
            JukeboxAPI.getShowManager().getShow(args.getOne("userShow").get().toString()).play(toPlay);
        } else {
            Player player = Sponge.getServer().getPlayer(args.<String>getOne("userShow").get()).get();
            if (player.isOnline()) {
                JukeboxAPI.play(player, toPlay);
            } else {
                HashMap<String, String> findAndReplace = new HashMap<String, String>();
                findAndReplace.put("user", args.getOne("userShow").get().toString());
                src.sendMessage(Text.builder(langManager.get("command.notOnline") + findAndReplace).build());
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
