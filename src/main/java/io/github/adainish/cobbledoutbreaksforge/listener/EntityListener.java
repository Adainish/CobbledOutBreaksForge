package io.github.adainish.cobbledoutbreaksforge.listener;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityListener
{
    @SubscribeEvent
    public void onTick(LivingEvent.LivingTickEvent event)
    {
        if (event.isCanceled())
            return;
        if (event.getEntity() instanceof PokemonEntity pokemonEntity) {
            if (pokemonEntity.getPersistentData().getBoolean("outbreakmon")) {
                if (RandomHelper.getRandom().nextDouble() < 0.4) {
                    CobbledOutBreaksForge.getServer().getPlayerList().getPlayers().stream().filter(pl -> pl.distanceTo(pokemonEntity) < 30).forEach(pl -> {
                        ServerLevel level = pl.getLevel();
                        level.sendParticles(ParticleTypes.ASH, pokemonEntity.getX(), pokemonEntity.getY(), pokemonEntity.getZ(), 0,
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
