package io.github.adainish.cobbledoutbreaksforge.obj;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OutBreak {
    public Species species;
    public int time = 5;

    public int maxPokemon = 6;

    public long started;

    public OutBreakLocation outBreakLocation;

    public double shinyChance = 1;

    public transient Task runnableTask;

    public OutBreak() {

    }

    public void setSpecies() {
        Species randomSpecies = RandomHelper.getRandomElementFromCollection(PokemonSpecies.INSTANCE.getSpecies());
        this.species = randomSpecies;
    }

    public boolean shouldSpawnNewPokemon() {
        return currentOutBreakAmount() < maxPokemon;
    }

    public void killAllOutBreakMons()
    {
        for (PokemonEntity entity:getWildOutBreakMons()) {
            entity.setHealth(-1);
        }
        CobbledOutBreaksForge.getLog().warn("Cleared out all outbreak Pokemon");
    }

    public List<PokemonEntity> getWildOutBreakMons()
    {
        MinecraftServer server = CobbledOutBreaksForge.getServer();
        BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
        BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
        AABB isWithinAABB = new AABB(pos1, pos2);
        List<PokemonEntity> pokemonEntityList = new ArrayList<>(server.overworld().getEntitiesOfClass(PokemonEntity.class, isWithinAABB));
        List<PokemonEntity> actualList = new ArrayList<>();
        for (PokemonEntity entity : pokemonEntityList) {
            if (entity.getSpecies().get().equals(species.getName()) && entity.getPersistentData().getBoolean("outbreakmon"))
                actualList.add(entity);
        }
        return actualList;
    }
    public int currentOutBreakAmount() {
        MinecraftServer server = CobbledOutBreaksForge.getServer();
        BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
        BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
        AABB isWithinAABB = new AABB(pos1, pos2);
        int outbreakCounter = 0;
        List<PokemonEntity> pokemonEntityList = new ArrayList<>(server.overworld().getEntitiesOfClass(PokemonEntity.class, isWithinAABB));
        System.out.println(species.getName());
        for (PokemonEntity entity : pokemonEntityList) {
            System.out.println(entity.getSpecies().get());
            if (entity.getSpecies().get().equals(species.getName()) && entity.getPersistentData().getBoolean("outbreakmon"))
                outbreakCounter++;
        }
        System.out.println("Outbreak counter for %speciestype% = %amount%".replace("%speciestype%", species.getName()).replace("%amount%", String.valueOf(outbreakCounter)));
        return outbreakCounter;
    }

    public boolean expired() {
        return System.currentTimeMillis() >= (started + TimeUnit.MINUTES.toMillis(time));
    }

    public void spawnPokemon() {
        if (shouldSpawnNewPokemon()) {
            MinecraftServer server = CobbledOutBreaksForge.getServer();
            BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
            BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
            AABB isWithinAABB = new AABB(pos1, pos2);
            List<ServerPlayer> playerList = new ArrayList<>(server.overworld().getEntitiesOfClass(ServerPlayer.class, isWithinAABB));
            ServerPlayer nearestPlayer = RandomHelper.removeRandomElementFromList(playerList);

            PokemonProperties pokemonProperties = new PokemonProperties();
            pokemonProperties.setSpecies(species.getName());
            if (RandomHelper.getRandomChance(shinyChance))
                pokemonProperties.setShiny(true);
            if (nearestPlayer != null) {
                if (pokemonProperties.getSpecies() != null) {
                    PokemonEntity pokemonEntity = pokemonProperties.createEntity(nearestPlayer.getLevel());
                    pokemonEntity.getPersistentData().putBoolean("outbreakmon", true);
                    double newX = RandomHelper.getRandomNumberBetween(outBreakLocation.minX, outBreakLocation.maxX);
                    double newY = RandomHelper.getRandomNumberBetween(outBreakLocation.minY, outBreakLocation.maxY);//?Replace with highest block y?
                    double newZ = RandomHelper.getRandomNumberBetween(outBreakLocation.minZ, outBreakLocation.maxZ);
                    pokemonEntity.setPos(newX, newY, newZ);
                } else {
                    System.out.println("Species null");
                }
            }
        }
    }
}
