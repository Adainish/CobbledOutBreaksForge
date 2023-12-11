package io.github.adainish.cobbledoutbreaksforge.obj;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;

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
