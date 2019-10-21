package net.mcjukebox.plugin.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class HelpCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.builder("Jukebox Commands :").color(TextColors.GREEN).build());
        src.sendMessage(Text.builder("/jukebox music <username/@show> <url> {options}").build());
        src.sendMessage(Text.builder("/jukebox sound <username/@show> <url> {options}").build());
        src.sendMessage(Text.builder("/jukebox stop <username/@show>").build());
        src.sendMessage(Text.builder("/jukebox stop <music/all> <username/@show> {options}").build());
        src.sendMessage(Text.builder("/jukebox region add <id> <url/@show>").build());
        src.sendMessage(Text.builder("/jukebox region remove <id>").build());
        src.sendMessage(Text.builder("/jukebox region list").build());
        src.sendMessage(Text.builder("/jukebox show add/remove <username> <@show>").build());
        src.sendMessage(Text.builder("/jukebox setkey <apikey>").build());

        return CommandResult.success();
    }
}
