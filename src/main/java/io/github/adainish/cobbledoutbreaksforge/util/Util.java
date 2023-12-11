package io.github.adainish.cobbledoutbreaksforge.util;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public static ResourceKey<Level> getDimension(String dimension) {
        return dimension.isEmpty() ? null : getDimension(ResourceLocationHelper.of(dimension));
    }

    public static ResourceKey<Level> getDimension(ResourceLocation key) {
        return ResourceKey.create(Registries.DIMENSION, key);
    }

    public static Optional<ServerLevel> getWorld(ResourceKey<Level> key) {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getLevel(key));
    }

    public static Optional<ServerLevel> getWorld(String key) {
        return getWorld(getDimension(key));
    }
}
