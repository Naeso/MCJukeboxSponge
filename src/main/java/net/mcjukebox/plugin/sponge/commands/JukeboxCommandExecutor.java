package net.mcjukebox.plugin.sponge.commands;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class JukeboxCommandExecutor{

    private MCJukebox currentInstance;
    private CommandSpec mainCommandJukebox;
    private RegionManager regionManager;

    public JukeboxCommandExecutor(RegionManager regionManager, MCJukebox instance) {
        this.regionManager = regionManager;
        this.currentInstance = instance;
        buildCommands();
        registerCommands();
    }

    private void buildCommands(){
        CommandSpec helpCommand = CommandSpec.builder()
                .description(Text.of("help command"))
                .executor(new HelpCommand(currentInstance))
                .build();

        CommandSpec musicCommand = CommandSpec.builder()
                .description(Text.of("music command"))
                .executor(new PlayCommand(currentInstance, ResourceType.MUSIC))
                .arguments(
                        GenericArguments.string(Text.of("UserOrShow")),
                        GenericArguments.string(Text.of("URL")),
                        GenericArguments.optionalWeak(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        CommandSpec soundCommand = CommandSpec.builder()
                .description(Text.of("sound command"))
                .executor(new PlayCommand(currentInstance, ResourceType.SOUND_EFFECT))
                .arguments(
                        GenericArguments.string(Text.of("UserOrShow")),
                        GenericArguments.string(Text.of("URL")),
                        GenericArguments.optionalWeak(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        CommandSpec regionCommand = CommandSpec.builder()
                .description(Text.of("region command"))
                .executor(new RegionCommand(regionManager, currentInstance))
                .arguments(
                        GenericArguments.string(Text.of("AddRemoveUpdateList")),
                        GenericArguments.optionalWeak(GenericArguments.string(Text.of("regionName"))),
                        GenericArguments.optionalWeak(GenericArguments.string(Text.of("URL")))
                )
                .build();

        CommandSpec setKeyCommand = CommandSpec.builder()
                .description(Text.of("setkey api command"))
                .executor(new SetKeyCommand(currentInstance))
                .arguments(
                        GenericArguments.string(Text.of("apikey"))
                )
                .build();

        CommandSpec showCommand = CommandSpec.builder()
                .description(Text.of("show command"))
                .executor(new ShowCommand(currentInstance))
                .arguments(
                        GenericArguments.string(Text.of("addRemove")),
                        GenericArguments.string(Text.of("targetPlayer")),
                        GenericArguments.string(Text.of("showId"))
                )
                .build();

        CommandSpec stopCommand = CommandSpec.builder()
                .description(Text.of("stop command"))
                .executor(new StopCommand(currentInstance))
                .arguments(
                        GenericArguments.string(Text.of("UserOrShow")),
                        GenericArguments.optionalWeak(GenericArguments.string(Text.of("MusicOrAll"))),
                        GenericArguments.optionalWeak(GenericArguments.remainingJoinedStrings(Text.of("options")))
                )
                .build();

        if(currentInstance.doesUniverseGuardIsPresent()){
            mainCommandJukebox = CommandSpec.builder()
                    .description(Text.of("Main plugin command"))
                    .executor(new JukeboxCommand(currentInstance))
                    .child(helpCommand, "help")
                    .child(musicCommand, "music")
                    .child(soundCommand, "sound")
                    .child(regionCommand, "region")
                    .child(setKeyCommand, "setkey")
                    .child(stopCommand, "stop")
                    .child(showCommand, "show")
                    .build();
        }else{
            mainCommandJukebox = CommandSpec.builder()
                    .description(Text.of("Main plugin command"))
                    .executor(new JukeboxCommand(currentInstance))
                    .child(helpCommand, "help")
                    .child(musicCommand, "music")
                    .child(soundCommand, "sound")
                    .child(setKeyCommand, "setkey")
                    .child(stopCommand, "stop")
                    .child(showCommand, "show")
                    .build();
        }
    }

    private void registerCommands(){
        Sponge.getCommandManager().register(currentInstance, mainCommandJukebox, "jukebox");
    }
}