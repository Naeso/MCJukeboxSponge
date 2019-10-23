package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class HelpCommand implements CommandExecutor {
    private MCJukebox currentInstance;

    public HelpCommand(MCJukebox instance) {
        this.currentInstance = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder("Jukebox Commands :").color(TextColors.GREEN).build());
        src.sendMessage(Text.of("/jukebox music <username/@show/@a> <url> {options}"));
        src.sendMessage(Text.of("/jukebox sound <username/@show/@a> <url> {options}"));
        src.sendMessage(Text.of("/jukebox stop <username/@show/@a>"));
        src.sendMessage(Text.of("/jukebox stop <username/@show/@a> <music/all> {options}"));
        src.sendMessage(Text.of("/jukebox show add/remove <username> <@show>"));
        src.sendMessage(Text.of("/jukebox setkey <apikey>"));

        if(currentInstance.doesUniverseGuardIsPresent()){
            src.sendMessage(Text.of("/jukebox region add <id|'here'> <url/@show>"));
            src.sendMessage(Text.of("/jukebox region update <id|'here'> <url/@show>"));
            src.sendMessage(Text.of("/jukebox region remove <id|'here'>"));
            src.sendMessage(Text.of("/jukebox region list"));
        }


        return CommandResult.success();
    }
}
