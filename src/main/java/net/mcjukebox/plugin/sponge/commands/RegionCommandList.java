package net.mcjukebox.plugin.sponge.commands;

import com.universeguard.region.LocalRegion;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class RegionCommandList implements CommandExecutor {

    private MCJukebox currentInstance;
    private JukeboxAPI jukeboxAPI;
    private RegionManager regionManager;
    private LocalRegion currentRegion;
    private String commandChoice;
    private String idRegion;
    private String UrlRegion;
    private Optional<Player> playerPresence;

    public RegionCommandList(RegionManager regionManager, MCJukebox instance) {
        this.currentInstance = instance;
        this.regionManager = regionManager;
        this.jukeboxAPI = new JukeboxAPI(instance);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // region list
        /*if(commandChoice.equals("list")) {
            src.sendMessage(Text.builder("Registered Regions (" + regionManager.getRegions().size() + "):").color(TextColors.GREEN).build());
            for (String region : regionManager.getRegions().keySet()) {
                src.sendMessage(Text.of(""));
                src.sendMessage(Text.builder("Name:").color(TextColors.GOLD).append(
                        Text.of(region)
                ).build());
                src.sendMessage(Text.builder("URL/Show:").color(TextColors.GOLD).append(
                        Text.of(regionManager.getRegions().get(region))
                ).build());

                return CommandResult.success();
            }
        }*/
        return CommandResult.empty();
    }
}
