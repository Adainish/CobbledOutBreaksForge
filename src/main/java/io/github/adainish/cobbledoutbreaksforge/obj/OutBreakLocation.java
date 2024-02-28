package io.github.adainish.cobbledoutbreaksforge.obj;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaks;

public class OutBreakLocation
{

    public String id = "example";
    public String playerName = "";
    public int minX = 0;
    public int minY = 0;
    public int minZ = 0;
    public int maxX = 0;
    public int maxY = 0;
    public int maxZ = 0;

    public OutBreakLocation()
    {

    }

    public String prettyLocation()
    {
        String pretty = CobbledOutBreaks.config.locationPlaceHolder;
        pretty = pretty
                .replace("%minx%", String.valueOf(minX))
                .replace("%maxx%", String.valueOf(maxX))
                .replace("%miny%", String.valueOf(minY))
                .replace("%maxy%", String.valueOf(maxY))
                .replace("%minz%", String.valueOf(minZ))
                .replace("%maxz%", String.valueOf(maxZ))
                .replace("%player%", playerName)
        ;
        return pretty;
    }
}
