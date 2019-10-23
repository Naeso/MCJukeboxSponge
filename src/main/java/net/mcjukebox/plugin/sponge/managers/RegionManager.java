package net.mcjukebox.plugin.sponge.managers;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import net.mcjukebox.shared.utils.DatabaseUtils;
import org.json.JSONException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class RegionManager{

    private JukeboxAPI api;
    private DataUtils dataUtils;
    private DatabaseUtils databaseUtils;

    private HashMap<String, String> regions;
    private Path folder;

    private MCJukebox currentInstance;

    public RegionManager(MCJukebox instance){
        folder = instance.privateConfigDir;
        this.currentInstance = instance;
        dataUtils = new DataUtils();
        databaseUtils = new DatabaseUtils();
        api = new JukeboxAPI(instance);
    }

    public void addRegion(String ID, String URL)
    {
        if (!databaseUtils.doesIDRegionExistsInDatabase(ID)){
            databaseUtils.setNewRegion(ID, URL);
        }else{
            currentInstance.logger.info("This region already exists !");
        }
    }

    public void updateRegion(String ID, String URL){
        HashMap<UUID, UUID> playerInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        if (databaseUtils.doesIDRegionExistsInDatabase(ID)){
            databaseUtils.updateURLRegion(ID, URL);
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(UUID.fromString(ID))) {
                    playerInRegion.remove(player.getPlayer().get().getUniqueId());
                    api.stopMusic(player.getPlayer().get());
                    Media media = new Media(ResourceType.MUSIC, getURL(UUID.fromString(ID)), currentInstance);
                    api.play(player, media);
                }
            }
        }else{
            currentInstance.logger.info("This region dosen't exists !");
        }
    }

    public void removeRegion(String ID) throws JSONException {
        /*ShowManager showManager = currentInstance.getShowManager();*/
        HashMap<UUID, UUID> playerInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        if (databaseUtils.doesIDRegionExistsInDatabase(ID)){
            databaseUtils.deleteRegion(ID);
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(UUID.fromString(ID))) {
                    playerInRegion.remove(player.getPlayer().get().getUniqueId());
                    api.stopMusic(player.getPlayer().get());
                }
            }
        }else{
            currentInstance.logger.info("This region dosen't exist !");
        }
    }

    public boolean hasRegion(String ID){
        return databaseUtils.doesIDRegionExistsInDatabase(ID);
    }

    public String getURL(UUID ID){
        return databaseUtils.getURLRegion(ID.toString());
    }
}
