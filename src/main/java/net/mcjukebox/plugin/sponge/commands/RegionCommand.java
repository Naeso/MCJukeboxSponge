package net.mcjukebox.plugin.sponge.commands;

import lombok.AllArgsConstructor;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@AllArgsConstructor
public class RegionCommand implements CommandExecutor {

    private RegionManager regionManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // region add <id> <url>
        if(args.getOne("addRemoveList").get().equals("add")){
            if (args.hasAny("idRegion")){
                if (args.hasAny("urlMusic")){
                    MCJukebox.getInstance().getRegionManager().addRegion(args.getOne("idRegion").get().toString(), args.getOne("urlMusic").get().toString());
                    MessageUtils.sendMessage(src, "region.registered");
                    return CommandResult.success();
                }
                else{
                    src.sendMessage(Text.of("Error : Must provide URL in order to add region."));
                    return CommandResult.empty();
                }
            }
            else{
                src.sendMessage(Text.of("Error : Must provide ID of the region in order to add region."));
                return CommandResult.empty();
            }
        }

        // region remove <id>
        if(args.getOne("addRemoveList").get().equals("remove")){
            if(MCJukebox.getInstance().getRegionManager().hasRegion(args.getOne("idRegion").get().toString())){
                MCJukebox.getInstance().getRegionManager().removeRegion(args.getOne("idRegion").get().toString());
                MessageUtils.sendMessage(src, "region.unregistered");
            }else{
                MessageUtils.sendMessage(src, "region.notregistered");
            }
            return CommandResult.success();
        }

        // region list
        if(args.getOne("addRemoveList").get().equals("list")) {
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
        }

        return CommandResult.empty();
    }
}
