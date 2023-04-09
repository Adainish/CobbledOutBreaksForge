package io.github.adainish.cobbledoutbreaksforge.obj;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;

public class OutBreakLocation
{

    public String id = "example";
    public double minX = 0.0D;
    public double minY = 0.0D;
    public double minZ = 0.0D;
    public double maxX = 0.0D;
    public double maxY = 0.0D;
    public double maxZ = 0.0D;

    public OutBreakLocation()
    {

    }

    public String prettyLocation()
    {
        String pretty = CobbledOutBreaksForge.config.locationPlaceHolder;
        pretty = pretty
                .replace("%minx%", String.valueOf(minX))
                .replace("%maxx%", String.valueOf(maxX))
                .replace("%miny%", String.valueOf(minY))
                .replace("%maxy%", String.valueOf(maxY))
                .replace("%minz%", String.valueOf(minZ))
                .replace("%maxz%", String.valueOf(maxZ))
        ;
        return pretty;
    }
}
