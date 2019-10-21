package net.mcjukebox.plugin.sponge.managers;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import org.json.JSONException;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class RegionManager{

    private JukeboxAPI api;
    public HashMap<String, String> getRegions() {
        return regions;
    }

    private HashMap<String, String> regions;
    private Path folder;

    private MCJukebox currentInstance;

    public RegionManager(MCJukebox instance){
        folder = instance.privateConfigDir;
        this.currentInstance = instance;
        api = new JukeboxAPI(instance);
        load();
    }

    private void load(){
        regions = DataUtils.loadObjectFromPath(folder.resolve("/regions.data"));
        if(regions == null) regions = new HashMap<>();

        // Import from the "shared.data" file we accidentally created
        HashMap<String, String> sharedFile = DataUtils.loadObjectFromPath(folder.resolve("/shared.data"));
        if (sharedFile != null) {
            currentInstance.logger.info("Running migration of shared.data regions...");
            for (String key : sharedFile.keySet()) regions.put(key, sharedFile.get(key));
            new File(folder + "/shared.data").delete();
            currentInstance.logger.info("Migration complete.");
        }
    }

    public void save(){
        DataUtils.saveObjectToPath(regions, folder.resolve("/regions.data"));
    }

    public void addRegion(String ID, String URL){
        regions.put(ID.toLowerCase(), URL);
    }

    public void removeRegion(String ID) throws JSONException {
        ShowManager showManager = currentInstance.getShowManager();
        HashMap<UUID, String> playersInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            String regionID = playersInRegion.get(uuid);

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
        return regions.containsKey(ID);
    }

    public String getURL(String ID){
        return regions.get(ID);
    }
}
