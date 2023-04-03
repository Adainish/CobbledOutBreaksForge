package io.github.adainish.cobbledoutbreaksforge.util;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Util
{
    public static MinecraftServer server = CobbledOutBreaksForge.getServer();

    private static final MinecraftServer SERVER = server;


    public static ItemStack returnIcon(Pokemon pokemon) {
        return PokemonItem.from(pokemon, 1);
    }

    public static Optional<ServerPlayer> getPlayerOptional(String name) {
        return Optional.ofNullable(CobbledOutBreaksForge.getServer().getPlayerList().getPlayerByName(name));
    }


    public static ServerPlayer getPlayer(String playerName) {
        return server.getPlayerList().getPlayerByName(playerName);
    }

    public static ServerPlayer getPlayer(UUID uuid) {
        return server.getPlayerList().getPlayer(uuid);
    }

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

    public static Pokemon generatePokemon(boolean legend, boolean ultrabeast, boolean shiny, boolean any) {
        Pokemon p = null;

        Species sp = null;

        if (legend)
            sp = RandomHelper.getRandomElementFromCollection(legendaryList());
        else if (ultrabeast)
            sp = RandomHelper.getRandomElementFromCollection(ultrabeastList());
        else if (any)
            sp = RandomHelper.getRandomElementFromCollection(pokemonList());
        else sp = RandomHelper.getRandomElementFromCollection(nonSpecialList());


        Pokemon spec = sp.create(1);

        if (p == null)
            return null;
        if(shiny)
            p.setShiny(true);

        return p;
    }

    public static ArrayList <String> pokemonLore(Pokemon p) {
        ArrayList<String> list = new ArrayList<>();
        list.add("&7Ball:&e " + p.getCaughtBall().getName().getPath().replace("_", " "));
        list.add("&7Ability:&e " + p.getAbility().getName().toLowerCase());
        list.add("&7Nature:&e " + p.getNature().getDisplayName());
        list.add("&7Gender:&e " + p.getGender().name().toLowerCase());

//        if (p.getPalette().getTexture() != null) {
//            if (!p.getPalette().getEmissiveTexture()) {
//                list.add("&5Custom Texture: &b" + p.getCustomTexture());
//            }
//        }
        list.add("&7IVS: (&f%ivs%%&7)".replace("%ivs%", String.valueOf("TBI")));
        list.add("&cHP: %hp% &7/ &6Atk: %atk% &7/ &eDef: %def%"
                .replace("%hp%", String.valueOf(p.getIvs().get(Stats.HP)))
                .replace("%atk%", String.valueOf(p.getIvs().get(Stats.ATTACK)))
                .replace("%def%", String.valueOf(p.getIvs().get(Stats.DEFENCE)))
        );

        list.add("&9SpA: %spa% &7/ &aSpD: %spd% &7/ &dSpe: %spe%"
                .replace("%spa%", String.valueOf(p.getIvs().get(Stats.SPECIAL_ATTACK)))
                .replace("%spd%", String.valueOf(p.getIvs().get(Stats.SPECIAL_DEFENCE)))
                .replace("%spe%", String.valueOf(p.getIvs().get(Stats.SPEED)))
        );

        list.add("&7EVS: (&f%evs%%&7)".replace("%evs%", String.valueOf(getEVSPercentage(1, p))));
        list.add("&cHP: %hp% &7/ &6Atk: %atk% &7/ &eDef: %def%"
                .replace("%hp%", String.valueOf(p.getEvs().get(Stats.HP)))
                .replace("%atk%", String.valueOf(p.getEvs().get(Stats.HP)))
                .replace("%def%", String.valueOf(p.getEvs().get(Stats.HP)))
        );

        list.add("&9SpA: %spa% &7/ &aSpD: %spd% &7/ &dSpe: %spe%"
                .replace("%spa%", String.valueOf(p.getEvs().get(Stats.SPECIAL_ATTACK)))
                .replace("%spd%", String.valueOf(p.getEvs().get(Stats.SPECIAL_DEFENCE)))
                .replace("%spe%", String.valueOf(p.getEvs().get(Stats.SPEED)))
        );


        for (Move m:p.getMoveSet().getMoves()) {
            if (m == null)
                continue;
            list.add("&7- " + m.getDisplayName().plainCopy());
        }

        return list;
    }

    public static int[] getEvsArray(Pokemon p) {
        return new int[]{p.getEvs().get(Stats.HP),
                p.getEvs().get(Stats.ATTACK),
                p.getEvs().get(Stats.DEFENCE),
                p.getEvs().get(Stats.SPECIAL_ATTACK),
                p.getEvs().get(Stats.SPECIAL_DEFENCE),
                p.getEvs().get(Stats.SPEED)};
    }

    public static double getEVSPercentage(int decimalPlaces, Pokemon p) {
        int total = 0;
        int[] evs = getEvsArray(p);

        for (int evStat : evs) {
            total += evStat;
        }

        double percentage = (double)total / 510.0D * 100.0D;
        return Math.floor(percentage * Math.pow(10.0D, decimalPlaces)) / Math.pow(10.0D, decimalPlaces);
    }

    public static Species getSpeciesFromString(String species)
    {
        Species sp = PokemonSpecies.INSTANCE.getByIdentifier(ResourceLocation.of("cobblemon:%sp%".replace("%sp%", species), ':'));
        if (sp == null)
            sp = PokemonSpecies.INSTANCE.getByIdentifier(ResourceLocation.of("cobblemon:vulpix", ':'));
        return sp;
    }

    public static void send(UUID uuid, String message) {
        getPlayer(uuid).sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }

    public static void send(CommandSource sender, String message) {
        sender.sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }

    public static void doBroadcast(String message) {
        SERVER.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            serverPlayerEntity.sendSystemMessage(Component.literal(TextUtil.getMessagePrefix().getString() + message.replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
        });
    }

    public static void doBroadcastPlain(String message) {
        SERVER.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            serverPlayerEntity.sendSystemMessage(Component.literal(message.replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
        });
    }


    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }

    public static List <String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList <>();
        for (String s:list) {
            formattedList.add(formattedString(s));
        }

        return formattedList;
    }
}
