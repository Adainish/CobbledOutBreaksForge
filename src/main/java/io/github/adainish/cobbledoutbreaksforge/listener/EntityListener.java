package io.github.adainish.cobbledoutbreaksforge.listener;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.adainish.cabled.events.EntityTickCallback;
import io.github.adainish.cobbledoutbreaksforge.util.RandomHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;


import java.util.Random;

public class EntityListener
{

    public EntityListener()
    {
        this.registerTick();
    }

    public void registerTick()
    {
        EntityTickCallback.EVENT.register(livingEntity -> {
            if (livingEntity instanceof PokemonEntity pokemonEntity)
            {
                if (pokemonEntity.getPokemon().getPersistentData().getBoolean("outbreakmon"))
                {
                    Random rand = RandomHelper.rand;
                    if (rand.nextDouble() < 0.4)
                    {
                        float w = pokemonEntity.getBbWidth();
                        float h = pokemonEntity.getBbHeight();
                        livingEntity.level().getNearbyPlayers(TargetingConditions.forNonCombat(), pokemonEntity, pokemonEntity.getBoundingBoxForCulling().inflate(30)).forEach(pl -> {
                            if (pl.level() instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(
                                        ParticleTypes.CRIMSON_SPORE,
                                        pokemonEntity.getX() + ((rand.nextDouble() * 2.0) - 1) * (h + 1),
                                        pokemonEntity.getY() + (rand.nextDouble() * (h + 1)),
                                        pokemonEntity.getZ() + (((rand.nextDouble() * 2.0) - 1) * (w + 1)),
                                        0,
                                        0D,
                                        1D,
                                        0D,
                                        1D);
                            }
                        });
                    }
                }
            }
            return InteractionResult.SUCCESS;
        });
    }


}
