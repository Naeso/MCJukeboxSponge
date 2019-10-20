package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class JukeboxCommandExecutor{

    CommandSpec mainCommandJukebox;
    RegionManager regionManager;

    public JukeboxCommandExecutor(RegionManager regionManager) {
        this.regionManager = regionManager;

        buildCommands();
        registerCommands();
    }

    private void buildCommands(){
        CommandSpec helpCommand = CommandSpec.builder()
                .description(Text.of("help command"))
                .executor(new HelpCommand())
                .build();

        CommandSpec musicCommand = CommandSpec.builder()
                .description(Text.of("music command"))
                .executor(new PlayCommand(ResourceType.MUSIC))
                .arguments(
                        GenericArguments.string(Text.of("usershow")),
                        GenericArguments.string(Text.of("URL")),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        CommandSpec soundCommand = CommandSpec.builder()
                .description(Text.of("sound command"))
                .executor(new PlayCommand(ResourceType.SOUND_EFFECT))
                .arguments(
                        GenericArguments.string(Text.of("usershow")),
                        GenericArguments.string(Text.of("URL")),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        CommandSpec RegionCommand = CommandSpec.builder()
                .description(Text.of("region command"))
                .executor(new RegionCommand(regionManager))
                .arguments(
                        GenericArguments.string(Text.of("addRemoveList")),
                        GenericArguments.string(Text.of("idRegion")),
                        GenericArguments.string(Text.of("urlMusic"))
                )
                .build();

        CommandSpec SetKeyCommand = CommandSpec.builder()
                .description(Text.of("setkey api command"))
                .executor(new HelpCommand())
                .arguments(
                        GenericArguments.string(Text.of("apikey"))
                )
                .build();

        CommandSpec showCommand = CommandSpec.builder()
                .description(Text.of("show command"))
                .executor(new HelpCommand())
                .arguments(
                        GenericArguments.string(Text.of("addRemove")),
                        GenericArguments.string(Text.of("targetPlayer")),
                        GenericArguments.string(Text.of("showId"))
                )
                .build();

        CommandSpec stopCommand = CommandSpec.builder()
                .description(Text.of("stop command"))
                .executor(new StopCommand())
                .arguments(
                        GenericArguments.string(Text.of("userShow")),
                        GenericArguments.string(Text.of("selectionMusicAll")),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        mainCommandJukebox = CommandSpec.builder()
                .description(Text.of("Main plugin command"))
                .executor(new JukeboxCommand())
                .child(helpCommand, "help")
                .child(musicCommand, "music")
                .child(soundCommand, "sound")
                .child(RegionCommand, "region")
                .child(SetKeyCommand, "setkey")
                .child(stopCommand, "stop")
                .child(showCommand, "show")
                .build();

    }

    private void registerCommands(){
        Sponge.getCommandManager().register(MCJukebox.getInstance(), mainCommandJukebox, "jukebox");
    }
}