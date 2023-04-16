package io.github.adainish.cobbledoutbreaksforge.obj;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;

public class OutBreakLocation
{

    public String id = "example";
    public String playerName = "";
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
                .replace("%minx%", String.valueOf((int) minX))
                .replace("%maxx%", String.valueOf((int) maxX))
                .replace("%miny%", String.valueOf((int) minY))
                .replace("%maxy%", String.valueOf((int) maxY))
                .replace("%minz%", String.valueOf((int) minZ))
                .replace("%maxz%", String.valueOf((int) maxZ))
                .replace("%player%", playerName)
        ;
        return pretty;
    }
}
