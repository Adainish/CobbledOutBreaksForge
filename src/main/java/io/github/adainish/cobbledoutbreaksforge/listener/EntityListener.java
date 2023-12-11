package io.github.adainish.cobbledoutbreaksforge.listener;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import kotlin.Unit;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class EntityListener
{
    @SubscribeEvent
    public void onTick(LivingEvent.LivingTickEvent event)
    {
        if (event.isCanceled())
            return;
        if (event.getEntity() == null)
            return;

        if (event.getEntity() instanceof PokemonEntity pokemonEntity) {
            if (pokemonEntity.getPersistentData().getBoolean("outbreakmon")) {
                Random rand = RandomHelper.rand;
                if (rand.nextDouble(1) < 0.4) {
                    float w = pokemonEntity.getBbWidth();
                    float h = pokemonEntity.getBbHeight();

                    CobbledOutBreaksForge.getServer().getPlayerList().getPlayers().stream().filter(pl -> pl.distanceTo(pokemonEntity) <= 30).forEach(pl -> {
                        ServerLevel level = pl.serverLevel();
                        level.sendParticles(
                                ParticleTypes.CRIMSON_SPORE,
                                pokemonEntity.getX() + ((rand.nextDouble() * 2.0) - 1) * (h + 1),
                                pokemonEntity.getY() + (rand.nextDouble() * (h + 1)),
                                pokemonEntity.getZ()+ (((rand.nextDouble() * 2.0) - 1) * (w + 1)),
                                0,
                                0D,
                                1D,
                                0D,
                                1D);
                    });
                }
            }
        }
    }

    public void subCap()
    {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {

            return Unit.INSTANCE;
        });
    }


}
