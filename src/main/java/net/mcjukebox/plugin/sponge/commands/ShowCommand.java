package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.managers.LangManager;
import net.mcjukebox.plugin.sponge.managers.shows.Show;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;

public class ShowCommand implements CommandExecutor {

    public static void setLangManager(LangManager langManager) {
        ShowCommand.langManager = langManager;
    }

    private static LangManager langManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!Sponge.getServer().getPlayer(args.<String>getOne("targetPlayer").get()).isPresent()) {
            HashMap<String, String> findAndReplace = new HashMap<String, String>();
            findAndReplace.put("user", Sponge.getServer().getPlayer(args.<String>getOne("targetPlayer").get()).toString());
            src.sendMessage(Text.builder(langManager.get("command.notOnline") + findAndReplace).build());
            return CommandResult.success();
        }

        Show show = MCJukebox.getInstance().getShowManager().getShow(args.<String>getOne("showId").get());
        Player target = Sponge.getServer().getPlayer(args.<String>getOne("targetPlayer").get()).get();

        switch (args.<String>getOne("addRemove").get()) {
            case "add":
                show.addMember(target, false);
                return CommandResult.success();
            case "remove":
                show.removeMember(target);
                return CommandResult.success();
            default:
                return CommandResult.empty();
        }
    }
}
