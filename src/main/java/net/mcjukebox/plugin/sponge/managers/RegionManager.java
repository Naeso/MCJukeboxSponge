package net.mcjukebox.plugin.sponge.managers;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import net.mcjukebox.shared.utils.DatabaseUtils;
import org.json.JSONException;
import org.spongepowered.api.Sponge;

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

    public void removeRegion(String ID) throws JSONException {
        ShowManager showManager = currentInstance.getShowManager();
        HashMap<UUID, UUID> playersInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            UUID regionID = playersInRegion.get(uuid);

            if (regionID.equals(ID)) {
                if (regions.get(ID).charAt(0) == '@') {
                    showManager.getShow(regions.get(ID)).removeMember(Sponge.getServer().getPlayer(uuid).get());
                } else {
                    api.stopMusic(Sponge.getServer().getPlayer(uuid).get());
                    keys.remove();
                }
            }
        }

        regions.remove(ID);
    }

    public boolean hasRegion(String ID){
        return databaseUtils.doesIDRegionExistsInDatabase(ID);
    }

    public String getURL(UUID ID){
        return databaseUtils.getURLRegion(ID.toString());
    }
}
