package io.github.adainish.cobbledoutbreaksforge.listener;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldListener
{
    @SubscribeEvent
    public void onTick(LivingEvent.LivingTickEvent event)
    {
        if (event.isCanceled())
            return;
        if (event.getEntity() instanceof PokemonEntity) {
            PokemonEntity pokemonEntity = (PokemonEntity) event.getEntity();
            if (pokemonEntity.getPersistentData().getBoolean("outbreakmon")) {
                if (RandomHelper.getRandom().nextDouble() < 0.4) {
                    CobbledOutBreaksForge.getServer().getPlayerList().getPlayers().stream().filter(pl -> pl.distanceTo(pokemonEntity) < 30).forEach(pl -> {
                        ServerLevel level = pl.getLevel();
                        level.sendParticles(pl, null, true, pokemonEntity.getX(), pokemonEntity.getY(), pokemonEntity.getZ(), 0,
                                0D,
                                1D,
                                0D,
                                1D);
                    });


                }
            }
        }
    }
}
