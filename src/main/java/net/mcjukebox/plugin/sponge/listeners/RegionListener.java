package net.mcjukebox.plugin.sponge.listeners;

import com.universeguard.region.LocalRegion;
import com.universeguard.region.Region;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.RegionManager;
import net.mcjukebox.plugin.sponge.managers.shows.Show;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import com.universeguard.utils.RegionUtils;

import java.util.HashMap;
import java.util.UUID;

public class RegionListener{

    private MCJukebox currentInstance;
    private JukeboxAPI api;
    private LocalRegion currentRegion;
    private UUID currentRegionId;
    private RegionManager utils;
    public HashMap<UUID, UUID> getPlayerInRegion() {
        return playerInRegion;
    }

    private HashMap<UUID, UUID> playerInRegion = new HashMap<UUID, UUID>();

    public RegionListener(RegionManager utils, MCJukebox instance){
        this.utils = utils;
        this.currentInstance = instance;
        api = new JukeboxAPI(instance);
    }

    @Listener
    public void onJoin(Join event, @Root Player player) {
        EnityMove(event, player);
    }

    @Listener
    public void onTeleport(MoveEntityEvent.Teleport event, @Root Player player){
        EnityMove(event, player);
    }

    /*@Listener
    public void onMinecartMove(MoveEntityEvent event) {
        if (event.getTargetEntity().getVehicle().get().getPassengers().get(0) != null && event.getTargetEntity().getVehicle().get().getPassengers().get(0) instanceof Player) {
            Player player = (Player) event.getTargetEntity().getVehicle().get().getPassengers().get(0);
            EnityMove(event, player);
        }
    }*/

    @Listener
    public void onMove(MoveEntityEvent event, @Root Player player){
        if(event.getTargetEntity() instanceof Player){
            EnityMove(event, player);
        }
    }

    @Listener
    public void onLeave(Disconnect event){
        if(playerInRegion.containsKey(event.getTargetEntity().getUniqueId())) {
            String lastAudio = utils.getURL(playerInRegion.get(event.getTargetEntity().getUniqueId()));

            if(lastAudio == null || lastAudio.toCharArray()[0] != '@') {
                api.stopMusic(event.getTargetEntity());
            }

            playerInRegion.remove(event.getTargetEntity().getUniqueId());
        }

        ShowManager showManager = currentInstance.getShowManager();
        if(showManager.inInShow(event.getTargetEntity().getUniqueId())) {
            for(Show show : showManager.getShowsByPlayer(event.getTargetEntity().getUniqueId())) {
                //Only run if they were added by a region
                if (!show.getMembers().get(event.getTargetEntity().getUniqueId())) return;
                show.removeMember(event.getTargetEntity());
            }
        }
    }

    private void EnityMove(Event event, @Root Player player){
        //Only execute if the player moves an entire block
        if(!(player.getLocation().getY() != player.getLocation().getX() || player.getLocation().getZ() != player.getLocation().getZ())) return;

        if(currentInstance.doesUniverseGuardIsPresent()){
            if (RegionUtils.getLocalRegion(player.getLocation()) == null) {
                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(currentRegionId)){
                    playerInRegion.remove(player.getPlayer().get().getUniqueId());
                    api.stopMusic(player.getPlayer().get());
                    return;
                }
            }
            else{
                currentRegion = RegionUtils.getLocalRegion(player.getLocation());
                currentRegionId = currentRegion.getId();
                ShowManager showManager = currentInstance.getShowManager();

                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(currentRegionId)) return;

                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        utils.getURL(playerInRegion.get(player.getPlayer().get().getUniqueId())).equals(
                                utils.getURL(currentRegionId))) {
                    // No need to restart the track, or re-add them to a show, but still update our records
                    playerInRegion.put(player.getPlayer().get().getUniqueId(), currentRegionId);
                    return;
                }

                /*if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId())) {
                    String lastShow = utils.getURL(playerInRegion.get(player.getPlayer().get().getUniqueId()));
                    if(lastShow.toCharArray()[0] == '@') {
                        showManager.getShow(lastShow).removeMember(player.getPlayer().get());

                }

                if(utils.getURL(currentRegionId).toCharArray()[0] == '@') {
                    if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId())) api.stopMusic(player.getPlayer().get());
                    showManager.getShow(utils.getURL(currentRegionId)).addMember(player.getPlayer().get(), true);
                    playerInRegion.put(player.getPlayer().get().getUniqueId(), currentRegionId);
                    return;
                }}*/

                Media media = new Media(ResourceType.MUSIC, utils.getURL(currentRegionId), currentInstance);
                api.play(player.getPlayer().get(), media);
                playerInRegion.put(player.getPlayer().get().getUniqueId(), currentRegionId);
            }
        }
    }
}
