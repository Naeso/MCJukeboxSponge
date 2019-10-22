package net.mcjukebox.plugin.sponge.commands;

import com.universeguard.region.LocalRegion;
import com.universeguard.utils.RegionUtils;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.json.JSONException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

public class RegionCommand implements CommandExecutor {

    private MCJukebox currentInstance;
    private RegionManager regionManager;
    private LocalRegion currentRegion;
    public RegionCommand(RegionManager regionManager, MCJukebox instance) {
        this.currentInstance = instance;
        this.regionManager = regionManager;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // region add <id> <url>
        if(args.getOne("AddRemoveUpdateList").get().equals("add")){
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
                src.sendMessage(Text.of("You must provide ID of the region in order to add region."));
                src.sendMessage(Text.of("You can also use the keyword 'here' instead of the id to select the current region you're in."));
                return CommandResult.empty();
            }
        }

        // region remove <id>
        if(args.getOne("AddRemoveUpdateList").get().equals("remove")){
            if (args.hasAny("idRegion")) {
                if (args.getOne("idRegion").get().equals("here") && (src instanceof Player)){
                    UUID idRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getId();
                    if (currentInstance.getRegionManager().hasRegion(idRegion.toString())) {
                        try {
                            currentInstance.getRegionManager().removeRegion(idRegion.toString());
                            return CommandResult.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MessageUtils.sendMessage(src, "region.unregistered");
                    }
                    return CommandResult.success();
                }else{
                    if (currentInstance.getRegionManager().hasRegion(args.getOne("idRegion").get().toString())) {
                        try {
                            currentInstance.getRegionManager().removeRegion(args.getOne("idRegion").get().toString());
                            return CommandResult.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MessageUtils.sendMessage(src, "region.unregistered");
                    }
                }
            } else {
                MessageUtils.sendMessage(src, "region.notregistered");
                src.sendMessage(Text.of("You must provide ID of the region in order to add region."));
                src.sendMessage(Text.of("You can also use the keyword 'here' instead of the id to select the current region you're in."));
                return CommandResult.empty();
            }

            return CommandResult.empty();
        }

        if(args.getOne("AddRemoveUpdateList").get().equals("update")) {
            if (args.hasAny("idRegion")) {
                if (args.getOne("idRegion").get().equals("here") && (src instanceof Player)){
                    UUID idRegion = RegionUtils.getLocalRegion(((Player) src).getLocation()).getId();
                    if (currentInstance.getRegionManager().hasRegion(idRegion.toString())) {
                        try {
                            currentInstance.getRegionManager().updateRegion(idRegion.toString(), args.<String>getOne("URL").get());
                            return CommandResult.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return CommandResult.empty();
                        }
                    }
                }else {
                    if (currentInstance.getRegionManager().hasRegion(args.getOne("idRegion").get().toString())) {
                        try {
                            currentInstance.getRegionManager().updateRegion(args.<String>getOne("idRegion").get(), args.<String>getOne("URL").get());
                            return CommandResult.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return CommandResult.empty();
                        }
                    }
                }
            } else {
                src.sendMessage(Text.of("You must provide ID of the region in order to add region."));
                src.sendMessage(Text.of("You can also use the keyword 'here' instead of the id to select the current region you're in."));
                return CommandResult.empty();
            }
            return CommandResult.empty();
        }

        // region list
        /*if(args.getOne("AddRemoveUpdateList").get().equals("list")) {
            src.sendMessage(Text.builder("Registered Regions (" + regionManager.getRegions().size() + "):").color(TextColors.GREEN).build());
            for(String region : regionManager.getRegions().keySet()) {
                src.sendMessage(Text.of(""));
                src.sendMessage(Text.builder("Name:").color(TextColors.GOLD).append(
                        Text.of(region)
                ).build());
                src.sendMessage(Text.builder("URL/Show:").color(TextColors.GOLD).append(
                        Text.of(regionManager.getRegions().get(region))
                ).build());
            }
            return CommandResult.success();
        }*/
        return CommandResult.empty();
    }
}
