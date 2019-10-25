package net.mcjukebox.plugin.sponge.commands;

import com.universeguard.region.LocalRegion;
import com.universeguard.utils.RegionUtils;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class RegionCommand implements CommandExecutor {

    private MCJukebox currentInstance;
    private JukeboxAPI jukeboxAPI;
    private RegionManager regionManager;
    private LocalRegion currentRegion;
    private String commandChoice;
    private String nameRegion;
    private String UrlRegion;
    private Optional<Player> playerPresence;

    public RegionCommand(RegionManager regionManager, MCJukebox instance) {
        this.currentInstance = instance;
        this.regionManager = regionManager;
        this.jukeboxAPI = new JukeboxAPI(instance);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // region add <id> <url>
        commandChoice = args.requireOne("AddRemoveUpdateList");

        if(commandChoice.equals("add")){
            if (args.hasAny("regionName")){
                if (args.hasAny("URL")){
                    if (args.getOne("regionName").get().equals("here") && (src instanceof Player)){
                        if(RegionUtils.getLocalRegion(((Player) src).getLocation()) == null){
                            src.sendMessage(Text.builder("You're not in a region !").color(TextColors.RED).build());
                            return CommandResult.empty();
                        }
                        nameRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getName();
                        currentInstance.getRegionManager().addRegion(nameRegion, args.<String>getOne("URL").get(), src);

                        return CommandResult.success();
                    }else{
                        currentInstance.getRegionManager().addRegion(args.<String>getOne("regionName").get(), args.<String>getOne("URL").get(), src);
                        return CommandResult.success();
                    }
                }
                else{
                    src.sendMessage(Text.builder("You must provide an URL in order to add region.").color(TextColors.RED).build());
                    return CommandResult.empty();
                }
            }
            else{
                errorCommandIdNotProvided(src);
                return CommandResult.empty();
            }
        }

        // region remove <id>
        if(commandChoice.equals("remove")){
            if (args.hasAny("regionName")) {
                if (args.getOne("regionName").get().equals("here") && (src instanceof Player)){
                    if(RegionUtils.getLocalRegion(((Player) src).getLocation()) == null){
                        src.sendMessage(Text.builder("You're not in a region !").color(TextColors.RED).build());
                        src.sendMessage(Text.builder("Tip : check if you recently changed region's name. If so, delete the past jukebox region and register it again.").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }

                    nameRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getName();

                    if (currentInstance.getRegionManager().hasRegion(nameRegion)) {
                        currentInstance.getRegionManager().removeRegion(nameRegion, src);
                        MessageUtils.sendMessage(src, "region.unregistered");
                        removeKeys(nameRegion);
                        return CommandResult.success();
                    }
                }else{
                    if (currentInstance.getRegionManager().hasRegion(args.getOne("regionName").get().toString())) {
                        currentInstance.getRegionManager().removeRegion(args.getOne("regionName").get().toString(), src);
                        MessageUtils.sendMessage(src, "region.unregistered");
                        removeKeys((args.getOne("regionName").get().toString()));
                        return CommandResult.success();
                    }
                }
            } else {
                MessageUtils.sendMessage(src, "region.notregistered");
                errorCommandIdNotProvided(src);
                return CommandResult.empty();
            }

            return CommandResult.empty();
        }

        if(commandChoice.equals("update")) {
            if (args.hasAny("regionName")) {
                if (args.hasAny("URL")){
                    if (args.requireOne("regionName").equals("here") && (src instanceof Player)){
                        if(RegionUtils.getLocalRegion(((Player) src).getLocation()) == null){
                            src.sendMessage(Text.builder("You're not in a region !").color(TextColors.RED).build());
                            src.sendMessage(Text.builder("Tip : check if you recently changed region's name. If so, delete the past jukebox region and register it again.").color(TextColors.RED).build());
                            return CommandResult.empty();
                        }

                        nameRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getName();

                        if (currentInstance.getRegionManager().hasRegion(nameRegion)) {
                            currentInstance.getRegionManager().updateRegion(nameRegion, args.requireOne("URL"), src);
                            return CommandResult.success();
                        }else{
                            src.sendMessage(Text.builder("Unknown region.").color(TextColors.RED).build());
                        }
                    }else {
                        if (currentInstance.getRegionManager().hasRegion(args.requireOne("regionName").toString())) {
                            currentInstance.getRegionManager().updateRegion(args.requireOne("regionName"), args.requireOne("URL"), src);
                            return CommandResult.success();
                        }
                    }
                } else{
                    src.sendMessage(Text.builder("You must provide an URL in order to update the region.").color(TextColors.RED).build());
                    return CommandResult.empty();
                }
            } else {
                errorCommandIdNotProvided(src);
                return CommandResult.empty();
            }
            return CommandResult.empty();
        }

        if(commandChoice.equals("list")) {
            src.sendMessage(Text.of("Sorry, this command is still being worked on."));
            return CommandResult.success();
        }

        src.sendMessage(Text.builder("This command does not exist !").color(TextColors.RED).build());
        return CommandResult.empty();
    }

    private void errorCommandIdNotProvided(CommandSource src){
        src.sendMessage(Text.builder("You must provide the name of the region in order to add region.").color(TextColors.RED).build());
        src.sendMessage(Text.builder("You can also use the keyword 'here' instead of the name to select the current region you're in.").color(TextColors.RED).build());
    }

    private void removeKeys(String nameRegion){
        HashMap<UUID, String> playersInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            String regionName = playersInRegion.get(uuid);

            if (regionName.equals(nameRegion)) {
                playerPresence = Sponge.getServer().getPlayer(uuid);
                if (playerPresence.isPresent()) {
                    jukeboxAPI.stopMusic(Sponge.getServer().getPlayer(uuid).get());
                    keys.remove();
                }
            }
        }
    }
}
