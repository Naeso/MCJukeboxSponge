package net.mcjukebox.regionproviders.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegionJuke {
    private String id;
    private int priority;
}
