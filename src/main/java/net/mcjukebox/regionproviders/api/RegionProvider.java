package net.mcjukebox.regionproviders.api;

import org.spongepowered.api.world.Location;

import java.util.List;

public interface RegionProvider {

    public String getName();
    public List<RegionJuke> getApplicableRegions(Location location);

}
