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
    private String idRegion;
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
            if (args.hasAny("idRegion")){
                if (args.hasAny("URL")){
                    if (args.getOne("idRegion").get().equals("here") && (src instanceof Player)){
                        UUID idRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getId();
                        currentInstance.getRegionManager().addRegion(idRegion.toString(), args.<String>getOne("URL").get());
                        MessageUtils.sendMessage(src, "region.registered");
                        return CommandResult.success();
                    }else{
                        currentInstance.getRegionManager().addRegion(args.<String>getOne("idRegion").get(), args.<String>getOne("URL").get());
                        MessageUtils.sendMessage(src, "region.registered");
                        return CommandResult.success();
                    }
                }
                else{
                    src.sendMessage(Text.of("You must provide URL in order to add region."));
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
            if (args.hasAny("idRegion")) {
                if (args.getOne("idRegion").get().equals("here") && (src instanceof Player)){
                    UUID idRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getId();
                    if (currentInstance.getRegionManager().hasRegion(idRegion.toString())) {
                        currentInstance.getRegionManager().removeRegion(idRegion.toString());
                        MessageUtils.sendMessage(src, "region.unregistered");
                        removeKeys(idRegion);
                        return CommandResult.success();
                    }
                }else{
                    if (currentInstance.getRegionManager().hasRegion(args.getOne("idRegion").get().toString())) {
                        currentInstance.getRegionManager().removeRegion(args.getOne("idRegion").get().toString());
                        MessageUtils.sendMessage(src, "region.unregistered");
                        removeKeys(UUID.fromString(args.getOne("idRegion").get().toString()));
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
            if (args.hasAny("idRegion")) {
                if (args.requireOne("idRegion").equals("here") && (src instanceof Player)){
                    UUID idRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getId();
                    if (currentInstance.getRegionManager().hasRegion(idRegion.toString())) {
                        currentInstance.getRegionManager().updateRegion(idRegion.toString(), args.requireOne("URL"));
                        return CommandResult.success();
                    }
                }else {
                    if (currentInstance.getRegionManager().hasRegion(args.requireOne("idRegion").toString())) {
                        currentInstance.getRegionManager().updateRegion(args.requireOne("idRegion"), args.requireOne("URL"));
                        return CommandResult.success();
                    }
                }
            } else {
                errorCommandIdNotProvided(src);
                return CommandResult.empty();
            }
            return CommandResult.empty();
        }
        return CommandResult.empty();
    }

    private void errorCommandIdNotProvided(CommandSource src){
        src.sendMessage(Text.builder("You must provide ID of the region in order to add region.").color(TextColors.RED).build());
        src.sendMessage(Text.builder("You can also use the keyword 'here' instead of the id to select the current region you're in.").color(TextColors.RED).build());
    }

    private void removeKeys(UUID idRegion){
        HashMap<UUID, UUID> playersInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            UUID regionID = playersInRegion.get(uuid);

            if (regionID.equals(idRegion)) {
                playerPresence = Sponge.getServer().getPlayer(uuid);
                if (playerPresence.isPresent()) {
                    jukeboxAPI.stopMusic(Sponge.getServer().getPlayer(uuid).get());
                    keys.remove();
                }
            }
        }
    }
}
