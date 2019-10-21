package net.mcjukebox.shared.utils;

import net.mcjukebox.plugin.sponge.MCJukebox;
import net.mcjukebox.regionproviders.api.RegionJuke;
import net.mcjukebox.regionproviders.api.RegionProvider;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {
    private static RegionUtils instance;
    private MCJukebox pluginInstance = new MCJukebox();

    public static RegionUtils getInstance() {
        if (instance == null) {
            instance = new RegionUtils();
        }
        return instance;
    }

    public RegionProvider getProvider() {
        if (pluginInstance.pluginManager.getPlugin("universeguard").isPresent()) {
            return new net.mcjukebox.regionproviders.RegionProvider();
        } else {
            return new RegionProvider() {
                public String getName() {
                    return null;
                }

                public List<RegionJuke> getApplicableRegions(Location location) {
                    return new ArrayList<RegionJuke>();
                }
            };
        }
    }

    private boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
