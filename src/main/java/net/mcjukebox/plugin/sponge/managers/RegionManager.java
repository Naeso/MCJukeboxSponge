package net.mcjukebox.plugin.sponge.managers;

import lombok.Getter;
import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class RegionManager{

    @Getter
    private HashMap<String, String> regions;
    private Path folder;

    public RegionManager(){
        folder = MCJukebox.privateConfigDir;

        load();
    }

    private void load(){
        regions = DataUtils.loadObjectFromPath(folder.resolve("/regions.data"));
        if(regions == null) regions = new HashMap<>();

        // Import from the "shared.data" file we accidentally created
        HashMap<String, String> sharedFile = DataUtils.loadObjectFromPath(folder.resolve("/shared.data"));
        if (sharedFile != null) {
            MCJukebox.logger.info("Running migration of shared.data regions...");
            for (String key : sharedFile.keySet()) regions.put(key, sharedFile.get(key));
            new File(folder + "/shared.data").delete();
            MCJukebox.logger.info("Migration complete.");
        }
    }

    public void save(){
        DataUtils.saveObjectToPath(regions, folder.resolve("/regions.data"));
    }

    public void addRegion(String ID, String URL){
        regions.put(ID.toLowerCase(), URL);
    }

    public void removeRegion(String ID){
        ShowManager showManager = MCJukebox.getInstance().getShowManager();
        HashMap<UUID, String> playersInRegion = MCJukebox.getInstance().getRegionListener().getPlayerInRegion();

        Iterator<UUID> keys = playersInRegion.keySet().iterator();

        while (keys.hasNext()) {
            UUID uuid = keys.next();
            String regionID = playersInRegion.get(uuid);

            if (regionID.equals(ID)) {
                if (regions.get(ID).charAt(0) == '@') {
                    showManager.getShow(regions.get(ID)).removeMember(Sponge.getServer().getPlayer(uuid).get());
                } else {
                    JukeboxAPI.stopMusic(Sponge.getServer().getPlayer(uuid).get());
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
