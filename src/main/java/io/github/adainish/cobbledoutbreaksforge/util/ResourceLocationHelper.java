package io.github.adainish.cobbledoutbreaksforge.util;

import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class ResourceLocationHelper
{

    public static ResourceLocation of(String resourceLocation) {
        try {
            return new ResourceLocation(resourceLocation.toLowerCase(Locale.ROOT));
        } catch (ResourceLocationException var2) {
            return null;
        }
    }
}
