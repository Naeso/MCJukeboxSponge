package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.json.JSONException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;

public class JukeboxCommand  implements CommandExecutor {

    private JukeboxAPI api;

    private MCJukebox currentInstance;

    public JukeboxCommand(MCJukebox instance) {
        this.currentInstance = instance;
        api = new JukeboxAPI(instance);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Warn user an API key is needed, unless they have one or are attempting to add one
        if (currentInstance.getAPIKey() == null) {
            src.sendMessage(Text.builder("No API Key set. Type /jukebox setkey <apikey>.").color(TextColors.DARK_RED).build());
            src.sendMessage(Text.builder("You can get this key from https://www.mcjukebox.net/admin").color(TextColors.DARK_RED).build());
            return CommandResult.empty();
        }
        else{
            if (src instanceof Player){
                URL(src);
                return CommandResult.success();
            }
            else{
                currentInstance.logger.info("I don't think consoles have ears, do they ?");
                return CommandResult.empty();
            }
        }
    }

    private boolean URL(final CommandSource sender) {
        MessageUtils.sendMessage(sender, "user.openLoading");
        Sponge.getScheduler().createAsyncExecutor(currentInstance).schedule(new Runnable() {
            @Override
            public void run() {
                if (!(sender instanceof Player)) return;
                String token = null;
                try {
                    token = api.getToken((Player) sender);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MessageUtils.sendURL((Player) sender, token);
            }
        }, 1, TimeUnit.SECONDS);
        return true;
    }
}
