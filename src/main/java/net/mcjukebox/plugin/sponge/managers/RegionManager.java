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
import org.spongepowered.api.text.format.TextColors;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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
            src.sendMessage(Text.builder("This region is already registered in MCJukebox !").color(TextColors.RED).build());
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
            src.sendMessage(Text.builder("Unknown region.").color(TextColors.RED).build());
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
            src.sendMessage(Text.builder("Unknown region.").color(TextColors.RED).build());
        }
    }

    public boolean hasRegion(String RegionName){
        return databaseUtils.doesRegionExistsInDatabase(RegionName);
    }

    public String getURL(String RegionName){
        return databaseUtils.getURLRegion(RegionName);
    }

    public List<Text> getAllRegisteredRegion(){
        return databaseUtils.getAllRegion();
    }
}
