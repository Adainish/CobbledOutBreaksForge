package io.github.adainish.cobbledoutbreaksforge.util;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class Util
{

    public static final long HOUR_IN_MILLIS = 3600000;
    public static final long MINUTE_IN_MILLIS = 60000;


    public static MinecraftServer server = CobbledOutBreaksForge.getServer();

    private static final MinecraftServer SERVER = server;

    public static List<Species> pokemonList() {
        return PokemonSpecies.INSTANCE.getImplemented();
    }

    public static List<Species> ultrabeastList() {
        List <Species> speciesList = new ArrayList<>(pokemonList());

        speciesList.removeIf(sp -> !sp.create(1).isUltraBeast());

        return speciesList;
    }

    public static List<Species> nonSpecialList() {
        List <Species> speciesList = new ArrayList <>(pokemonList());

        speciesList.removeIf(sp -> sp.create(1).isUltraBeast());

        speciesList.removeIf(sp -> sp.create(1).isLegendary());

        return speciesList;
    }

    public static List<Species> legendaryList() {

        List <Species> speciesList = new ArrayList <>(pokemonList());

        speciesList.removeIf(sp -> !sp.create(1).isLegendary());

        return speciesList;
    }

    public static Species getSpeciesFromString(String species)
    {
        Species sp = PokemonSpecies.INSTANCE.getByIdentifier(ResourceLocation.of("cobblemon:%sp%".replace("%sp%", species), ':'));
        if (sp == null)
            sp = PokemonSpecies.INSTANCE.getByIdentifier(ResourceLocation.of("cobblemon:vulpix", ':'));
        return sp;
    }


    public static void doBroadcast(String message) {
        SERVER.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            serverPlayerEntity.sendSystemMessage(Component.literal(TextUtil.getMessagePrefix().getString() + formattedString(message)));
        });
    }

    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }
}
