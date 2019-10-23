package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
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

import java.util.Collection;
import java.util.Optional;

public class PlayCommand implements CommandExecutor {

    private JukeboxAPI api;

    private MCJukebox currentInstance;
    private Optional<Player> playerPresence;
    private String targetPlayersShow;
    private Player targetPlayer;
    private String url;
    private Media toPlay;
    private ResourceType type;

    public PlayCommand(MCJukebox instance, ResourceType type) {
        this.currentInstance = instance;
        langManager = new LangManager();
        api = new JukeboxAPI(instance);
        this.type = type;
    }

    private LangManager langManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        targetPlayersShow = args.requireOne("UserOrShow");
        url = args.requireOne("URL");
        toPlay = new Media(type, url, currentInstance);

        if (args.hasAny("options")) {
            JSONObject options = jsonFromArgs((String[])args.getAll("options").toArray(), 2);

            if (options == null) {
                src.sendMessage(Text.builder("Unable to parse options as JSON.").color(TextColors.RED).build());
                return CommandResult.empty();
            }

            toPlay.loadOptions(options);
        }

        if (targetPlayersShow.startsWith("@")) {
            if (targetPlayersShow.matches("^@a$")) {
                Collection<Player> player = Sponge.getServer().getOnlinePlayers();
                for (Player user : player) {
                    api.play(user, toPlay);
                }
            } else{
                api.getShowManager().getShow(targetPlayersShow).play(toPlay);
                return CommandResult.success();
            }
        }
        else {
            playerPresence = Sponge.getServer().getPlayer(targetPlayersShow);
            if (playerPresence.isPresent()) {
                targetPlayer = playerPresence.get();
                api.play(targetPlayer, toPlay);
                return CommandResult.success();
            }
            else{
                src.sendMessage(Text.builder(langManager.get("command.notOnline")).color(TextColors.RED).build());
                return CommandResult.empty();
            }
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
