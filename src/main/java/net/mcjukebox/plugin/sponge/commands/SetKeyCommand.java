package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class SetKeyCommand implements CommandExecutor {

    private MCJukebox currentInstance;

    public SetKeyCommand(MCJukebox instance) {
        this.currentInstance = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            src.sendMessage(Text.builder("Trying key : " + args.<String>getOne("apikey").get()).color(TextColors.GREEN).build());
            currentInstance.getSocketHandler().getKeyHandler().tryKey(src, args.<String>getOne("apikey").get());
        } catch (IOException e) {
            e.printStackTrace();
            return CommandResult.empty();
        }
        return CommandResult.success();
    }
}
