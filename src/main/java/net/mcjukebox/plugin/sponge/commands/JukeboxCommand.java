package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
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

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Warn user an API key is needed, unless they have one or are attempting to add one
        if (MCJukebox.getInstance().getAPIKey() == null) {
            src.sendMessage(Text.builder("No API Key set. Type /jukebox setkey <apikey>.").color(TextColors.DARK_RED).build());
            src.sendMessage(Text.builder("You can get this key from https://www.mcjukebox.net/admin").color(TextColors.DARK_RED).build());
            return CommandResult.empty();
        }
        else{
            URL(src);
            return CommandResult.success();
        }
    }

    private boolean URL(final CommandSource sender) {
        MessageUtils.sendMessage(sender, "user.openLoading");
        Sponge.getScheduler().createAsyncExecutor(MCJukebox.getInstance()).schedule(new Runnable() {
            @Override
            public void run() {
                if (!(sender instanceof Player)) return;
                String token = JukeboxAPI.getToken((Player) sender);
                MessageUtils.sendURL((Player) sender, token);
            }
        }, 1, TimeUnit.SECONDS);
        return true;
    }
}
