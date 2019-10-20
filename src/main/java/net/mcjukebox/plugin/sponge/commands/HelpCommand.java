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
        src.sendMessage(Text.builder("Jukebox Commands :").color(TextColors.GREEN).append(
                Text.builder("/jukebox music <username/@show> <url> {options}").append(
                        Text.builder("/jukebox sound <username/@show> <url> {options}").append(
                                Text.builder("/jukebox stop <username/@show>").append(
                                        Text.builder("/jukebox stop <music/all> <username/@show> {options}").append(
                                                Text.builder("/jukebox region add <id> <url/@show>").append(
                                                        Text.builder("/jukebox region remove <id>").append(
                                                                Text.builder("/jukebox region list").append(
                                                                        Text.builder("/jukebox show add/remove <username> <@show>").append(
                                                                                Text.builder("/jukebox setkey <apikey>").build()
                                                                        ).build()
                                                                ).build()
                                                        ).build()
                                                ).build()
                                        ).build()
                                ).build()
                        ).build()
                ).build()
        ).build());

        return CommandResult.success();
    }
}
