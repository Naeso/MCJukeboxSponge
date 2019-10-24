package net.mcjukebox.plugin.sponge.managers;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.plugin.sponge.api.JukeboxAPI;
import net.mcjukebox.plugin.sponge.api.ResourceType;
import net.mcjukebox.plugin.sponge.api.models.Media;
import net.mcjukebox.plugin.sponge.managers.shows.ShowManager;
import net.mcjukebox.plugin.sponge.utils.DataUtils;
import net.mcjukebox.plugin.sponge.utils.MessageUtils;
import net.mcjukebox.shared.utils.DatabaseUtils;
import org.json.JSONException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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

    public void addRegion(String RegionName, String URL, CommandSource src)
    {
        if (!databaseUtils.doesRegionExistsInDatabase(RegionName)){
            databaseUtils.setNewRegion(RegionName, URL);
            MessageUtils.sendMessage(src, "region.registered");
        }else{
            src.sendMessage(Text.of("This region already exists !"));
        }
    }

    public void updateRegion(String RegionName, String URL, CommandSource src){
        HashMap<UUID, String> playerInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        if (databaseUtils.doesRegionExistsInDatabase(RegionName)){
            databaseUtils.updateURLRegion(RegionName, URL);
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(RegionName)) {
                    playerInRegion.remove(player.getPlayer().get().getUniqueId());
                    api.stopMusic(player.getPlayer().get());
                    Media media = new Media(ResourceType.MUSIC, getURL(RegionName), currentInstance);
                    api.play(player, media);
                }
            }
        }else{
            src.sendMessage(Text.of("This region dosen't exists !"));
        }
    }

    public void removeRegion(String RegionName, CommandSource src) throws JSONException {
        /*ShowManager showManager = currentInstance.getShowManager();*/
        HashMap<UUID, String> playerInRegion = currentInstance.getRegionListener().getPlayerInRegion();

        if (databaseUtils.doesRegionExistsInDatabase(RegionName)){
            databaseUtils.deleteRegion(RegionName);
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                if(playerInRegion.containsKey(player.getPlayer().get().getUniqueId()) &&
                        playerInRegion.get(player.getPlayer().get().getUniqueId()).equals(RegionName)) {
                    playerInRegion.remove(player.getPlayer().get().getUniqueId());
                    api.stopMusic(player.getPlayer().get());
                }
            }
        }else{
            src.sendMessage(Text.of("This region dosen't exist !"));
        }
    }

    public boolean hasRegion(String RegionName){
        return databaseUtils.doesRegionExistsInDatabase(RegionName);
    }

    public String getURL(String RegionName){
        return databaseUtils.getURLRegion(RegionName);
    }
}
