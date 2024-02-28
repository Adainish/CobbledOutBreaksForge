package io.github.adainish.cobbledoutbreaksforge.obj;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaks;
import io.github.adainish.cobbledoutbreaksforge.scheduler.AsyncScheduler;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import io.github.adainish.cobbledoutbreaksforge.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OutBreak {
    public Species species;
    public int time = 5;

    public long started = 0;

    public OutBreakLocation outBreakLocation;

    public int shinyChance = 1;
    public long lastSpawn = 0;

    public int totalSpawns = 0;

    public transient AsyncScheduler scheduler;

    public OutBreak() {

    }

    public void setSpecies() {
        Species selected = null;
        while (selected == null)
        {
            Species generated = RandomHelper.getRandomElementFromCollection(Util.pokemonList());
            if (!CobbledOutBreaks.config.whiteListed.isEmpty() && !CobbledOutBreaks.config.getWhiteListedSpecies().contains(species))
                continue;
            if (!CobbledOutBreaks.config.blackListed.isEmpty() && CobbledOutBreaks.config.getBlackListedSpecies().contains(generated))
                continue;
            if (!CobbledOutBreaks.config.allowLegends && generated.create(1).isLegendary())
                continue;
            if (!CobbledOutBreaks.config.allowUltraBeasts && generated.create(1).isUltraBeast())
                continue;
            selected = generated;

        }
        this.species = selected;
    }

    public boolean spawnTimerValid()
    {
        return System.currentTimeMillis() >= (lastSpawn = TimeUnit.SECONDS.toMillis(CobbledOutBreaks.config.secondsBetweenSpawns));
    }

    public boolean shouldSpawnNewPokemon() {
        return spawnTimerValid() && currentOutBreakAmount() < CobbledOutBreaks.config.maxSpawns && totalSpawns < CobbledOutBreaks.config.maxSpawns;
    }

    public void killAllOutBreakMons()
    {
        for (PokemonEntity entity:getWildOutBreakMons()) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
        CobbledOutBreaks.getLog().warn("Cleared out all outbreak Pokemon");
    }

    public List<PokemonEntity> getWildOutBreakMons()
    {
        MinecraftServer server = CobbledOutBreaks.getServer();
        BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
        BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
        AABB isWithinAABB = new AABB(pos1, pos2);
        List<PokemonEntity> actualList = new ArrayList<>();
        List<PokemonEntity> pokemonEntityList = new ArrayList<>(server.overworld().getEntitiesOfClass(PokemonEntity.class, isWithinAABB));
        for (PokemonEntity entity : pokemonEntityList) {
            String get = entity.getSpecies().get().replace("cobblemon:", "");
            if (get.equalsIgnoreCase(species.getName()) && entity.getPokemon().getPersistentData().getBoolean("outbreakmon"))
                actualList.add(entity);
        }
        return actualList;
    }
    public int currentOutBreakAmount() {
        MinecraftServer server = CobbledOutBreaks.getServer();
        BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
        BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
        AABB isWithinAABB = new AABB(pos1, pos2);
        int outbreakCounter = 0;
        List<PokemonEntity> pokemonEntityList = new ArrayList<>(server.overworld().getEntitiesOfClass(PokemonEntity.class, isWithinAABB));
        for (PokemonEntity entity : pokemonEntityList) {
            String get = entity.getSpecies().get().replace("cobblemon:", "");
            if (get.equalsIgnoreCase(species.getName()) && entity.getPokemon().getPersistentData().getBoolean("outbreakmon"))
                outbreakCounter++;
        }
        return outbreakCounter;
    }

    public long getExpirationTime()
    {
        return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(time);
    }

    public String timeLeft()
    {

        long cd = getExpirationTime() - System.currentTimeMillis();
        long hours = cd / Util.HOUR_IN_MILLIS;
        cd = cd - (hours * Util.HOUR_IN_MILLIS);
        long minutes = cd / Util.MINUTE_IN_MILLIS;

        return CobbledOutBreaks.config.timerPlaceHolder.replace("%hours%", String.valueOf(hours)).replace("%minutes%", String.valueOf(minutes));
    }

    public boolean expired() {
        return System.currentTimeMillis() >= (started + TimeUnit.MINUTES.toMillis(time));
    }

    public int getRandomChance() {
        return (int) (Math.floor(Math.random() * 100) + 1);
    }

    public void spawnPokemon() {
        if (shouldSpawnNewPokemon()) {
            MinecraftServer server = CobbledOutBreaks.getServer();
            BlockPos pos1 = new BlockPos(outBreakLocation.minX, outBreakLocation.minY, outBreakLocation.minZ);
            BlockPos pos2 = new BlockPos(outBreakLocation.maxX, outBreakLocation.maxY, outBreakLocation.maxZ);
            AABB isWithinAABB = new AABB(pos1, pos2);
            List<Player> playerList = new ArrayList<>(server.overworld().getEntitiesOfClass(Player.class, isWithinAABB));
            ServerPlayer nearestPlayer = (ServerPlayer) RandomHelper.getRandomElementFromCollection(playerList);
            if (nearestPlayer != null) {
                PokemonProperties pokemonProperties = new PokemonProperties();
                int randomLevel = RandomHelper.getRandomNumberBetween(CobbledOutBreaks.config.minLevel, CobbledOutBreaks.config.maxLevel);
                pokemonProperties.setLevel(randomLevel);
                pokemonProperties.setSpecies(species.getResourceIdentifier().getPath());
                if (pokemonProperties.getSpecies() != null) {
                    int newY = RandomHelper.getRandomNumberBetween(outBreakLocation.minY, outBreakLocation.maxY);//?Replace with highest block y?
                    int newZ = RandomHelper.getRandomNumberBetween(outBreakLocation.minZ, outBreakLocation.maxZ);
                    int newX = RandomHelper.getRandomNumberBetween(outBreakLocation.minX, outBreakLocation.maxX);

                    BlockPos pos = nearestPlayer.serverLevel().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(newX, newY, newZ));

                    int randomChance = getRandomChance();
                    if (randomChance <= shinyChance) {
                        pokemonProperties.setShiny(true);
                        SoundEvent soundEvent = SoundEvents.BLAZE_DEATH;
                        nearestPlayer.serverLevel().playSound(null, nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ(), soundEvent, SoundSource.PLAYERS, 100.0F, 100.0F);
                    }
                    PokemonEntity pokemonEntity = pokemonProperties.createEntity(nearestPlayer.serverLevel());
                    pokemonEntity.getPokemon().getPersistentData().putBoolean("outbreakmon", true);

                    pokemonEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
                    nearestPlayer.serverLevel().addFreshEntity(pokemonEntity);
                    totalSpawns++;
                    lastSpawn = System.currentTimeMillis();
                }
            }
        }
    }
}
