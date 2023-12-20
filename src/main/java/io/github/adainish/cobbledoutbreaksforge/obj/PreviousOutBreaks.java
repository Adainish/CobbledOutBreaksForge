package io.github.adainish.cobbledoutbreaksforge.obj;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.Adapters;
import io.github.adainish.cobbledoutbreaksforge.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreviousOutBreaks
{
    public List<OutBreak> previousOutBreaks = new ArrayList<>();

    public PreviousOutBreaks()
    {

    }

    public void addOutBreak(OutBreak outBreak)
    {
        previousOutBreaks.add(0, outBreak);
    }

    public ItemStack speciesToSprite(Species species)
    {
        return PokemonItem.from(species.create(1));
    }

    public List<Button> getButtons()
    {
        List<Button> buttons = new ArrayList<>();
        for (OutBreak outBreak:previousOutBreaks)
        {
            if (outBreak.getOptionalSpeciesFromID().isEmpty())
                continue;
            Species species = outBreak.getOptionalSpeciesFromID().get();
            buttons.add(GooeyButton.builder()
                            .title(Util.formattedString("&7Species: &b" + species.getName()))
                            .lore(Arrays.asList("Started: " + outBreak.started, "Total Spawns: " + outBreak.totalSpawns, "Shiny Chance: " + outBreak.shinyChance, "Last Spawn: " + outBreak.lastSpawn, "Time: " + outBreak.time, "Location: " + outBreak.outBreakLocation.playerName))
                            .display(speciesToSprite(species))
                    .build());
        }
        return buttons;
    }

    public LinkedPage getPage()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(GooeyButton.builder()
                .display(new ItemStack(Items.BLACK_STAINED_GLASS_PANE))
                .build());
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("&bPrevious Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("&bNext Page"))
                .linkType(LinkType.Next)
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), getButtons(), LinkedPage.builder().template(builder.build()));
    }


    public static void writeConfig()
    {
        File dir = CobbledOutBreaksForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        PreviousOutBreaks config = new PreviousOutBreaks();
        try {
            File file = new File(dir, "previousoutbreaks.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledOutBreaksForge.getLog().warn(e);
        }
    }

    public static PreviousOutBreaks getConfig()
    {
        File dir = CobbledOutBreaksForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "previousoutbreaks.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledOutBreaksForge.getLog().error("Something went wrong attempting to read the PreviousOutbreaks");
            return null;
        }

        return gson.fromJson(reader, PreviousOutBreaks.class);
    }

    public void save()
    {
        File dir = CobbledOutBreaksForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        try {
            File file = new File(dir, "previousoutbreaks.json");
            if (file.exists())
                file.delete();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(this);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledOutBreaksForge.getLog().warn(e);
        }
    }

    public void open(ServerPlayer serverPlayer)
    {
        UIManager.openUIForcefully(serverPlayer, getPage());
    }
}
