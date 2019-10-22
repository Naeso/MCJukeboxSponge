package net.mcjukebox.regionproviders;

import net.mcjukebox.regionproviders.api.RegionJuke;
import com.universeguard.region.LocalRegion;
import com.universeguard.utils.RegionUtils;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionProvider implements net.mcjukebox.regionproviders.api.RegionProvider {

    public String getName() {
        return "ug";
    }

    public List<RegionJuke> getApplicableRegions(Location location) {
        ArrayList<RegionJuke> regionList = new ArrayList<RegionJuke>();

        ArrayList<LocalRegion> regions = RegionUtils.getAllLocalRegionsAt(location);

        for (LocalRegion regionJuke : regions) {
            regionList.add(new RegionJuke(regionJuke.getId().toString(), regionJuke.getPriority()));
        }

        return regionList;
    }
}
