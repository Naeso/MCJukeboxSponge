package net.mcjukebox.plugin.sponge.listeners;

import lombok.Getter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.managers.shows.Show;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.regionproviders.api.RegionJuke;
import net.mcjukebox.shared.utils.RegionUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;

import java.util.HashMap;
import java.util.UUID;

public class RegionListener{

    private RegionManager utils;
    @Getter private HashMap<UUID, String> playerInRegion = new HashMap<UUID, String>();

    public RegionListener(RegionManager utils){
        this.utils = utils;
    }

    @Listener
    public void onJoin(Join event, @Root Player player) {
        EnityMove(event, player);
    }

    @Listener
    public void onTeleport(MoveEntityEvent.Teleport event, @Root Player player){
        EnityMove(event, player);
    }

    @Listener
    public void onMinecartMove(MoveEntityEvent event) {
        if (event.getTargetEntity().getVehicle().get().getPassengers().get(0) != null && event.getTargetEntity().getVehicle().get().getPassengers().get(0) instanceof Player) {
            Player player = (Player) event.getTargetEntity().getVehicle().get().getPassengers().get(0);
            EnityMove(event, player);
        }
    }

    @Listener
    public void onMove(MoveEntityEvent event, @Root Player player){
        EnityMove(event, player);
    }

    @Listener
    public void onLeave(Disconnect event){
        if(playerInRegion.containsKey(event.getTargetEntity().getUniqueId())) {
            String lastAudio = utils.getURL(playerInRegion.get(event.getTargetEntity().getUniqueId()));

            if(lastAudio == null || lastAudio.toCharArray()[0] != '@') {
                JukeboxAPI.stopMusic(event.getTargetEntity());
            }

            playerInRegion.remove(event.getTargetEntity().getUniqueId());
        }

        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        if(showManager.inInShow(event.getTargetEntity().getUniqueId())) {
            for(Show show : showManager.getShowsByPlayer(event.getTargetEntity().getUniqueId())) {
                //Only run if they were added by a region
                if (!show.getMembers().get(event.getTargetEntity().getUniqueId())) return;
                show.removeMember(event.getTargetEntity());
            }
        }
    }

    private void EnityMove(Event event, Player player){
        //Only execute if the player moves an entire block
        if(!(player.getLocation().getY() != player.getLocation().getX() || player.getLocation().getZ() != player.getLocation().getZ())) return;

        int highestPriority = -1;
        String highestRegion = null;
        for (RegionJuke regionJuke : RegionUtils.getInstance().getProvider().getApplicableRegions(player.getLocation())) {
            if (regionJuke.getPriority() > highestPriority && utils.hasRegion(regionJuke.getId())) {
                highestPriority = regionJuke.getPriority();
                highestRegion = regionJuke.getId();
            }
        }

        ShowManager showManager = MCJukebox.getInstance().getShowManager();

        if(highestRegion == null && utils.hasRegion("__global__")) {
            highestRegion = "__global__";
        }

        //In this case, there are no applicable shared so we need go no further
        if(highestRegion == null){
            if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId())){
                String lastShow = utils.getURL(playerInRegion.get(player.getPlayer().get().getUniqueId()));
                playerInRegion.remove(player.getPlayer().get().getUniqueId());

                if (lastShow == null || lastShow.toCharArray()[0] != '@') {
                    //Region no longer exists, stop the music.
                    JukeboxAPI.stopMusic(player.getPlayer().get());
                    return;
                } else if(lastShow.toCharArray()[0] == '@') {
                    showManager.getShow(lastShow).removeMember(player.getPlayer().get());
                    return;
                }
            }
            return;
        }

        if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(highestRegion)) return;

        if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                utils.getURL(playerInRegion.get(player.getPlayer().get().getUniqueId())).equals(
                        utils.getURL(highestRegion))) {
            // No need to restart the track, or re-add them to a show, but still update our records
            playerInRegion.put(player.getPlayer().get().getUniqueId(), highestRegion);
            return;
        }

        if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId())) {
            String lastShow = utils.getURL(playerInRegion.get(player.getPlayer().get().getUniqueId()));
            if(lastShow.toCharArray()[0] == '@') {
                showManager.getShow(lastShow).removeMember(player.getPlayer().get());
            }
        }

        if(utils.getURL(highestRegion).toCharArray()[0] == '@') {
            if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId())) JukeboxAPI.stopMusic(player.getPlayer().get());
            showManager.getShow(utils.getURL(highestRegion)).addMember(player.getPlayer().get(), true);
            playerInRegion.put(player.getPlayer().get().getUniqueId(), highestRegion);
            return;
        }

        Media media = new Media(ResourceType.MUSIC, utils.getURL(highestRegion));
        JukeboxAPI.play(player.getPlayer().get(), media);
        playerInRegion.put(player.getPlayer().get().getUniqueId(), highestRegion);
    }
}
