package io.github.adainish.cobbledoutbreaksforge.scheduler;

import java.util.function.Consumer;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
public class AsyncScheduler {

    private final int interval;
    private final int iterations;
    private final Runnable runnable;
    private final Consumer<Void> consumerTask;
    private int iterationCount = 0;
    private boolean expired = false;
    private int ticksElapsed = 0;

    private AsyncScheduler(int interval, int iterations, Runnable runnable, Consumer<Void> consumerTask) {
        this.interval = interval;
        this.iterations = iterations;
        this.runnable = runnable;
        this.consumerTask = consumerTask;
    }

    public void start() {
        if (interval <= 0) {
            throw new IllegalStateException("Interval must be greater than 0");
        }
        CobbledOutBreaksForge.getLog().debug("Starting async scheduler with interval of {} ticks and {} iterations", interval, iterations);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void stop() {
        expired = true;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    private void execute() {
        if (runnable != null) {
            runnable.run();
        } else if (consumerTask != null) {
            consumerTask.accept(null);
        }
        iterationCount++;
        if (iterations != 0 && iterationCount >= iterations) {
            expired = true;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (expired) {
                return;
            }
            ticksElapsed++;
            if (ticksElapsed >= interval) {
                ticksElapsed = 0;
                execute();
            }
        }
    }

    public static class Builder {
        private int interval = -1;
        private int iterations = 0;
        private Runnable runnable = null;
        private Consumer<Void> consumerTask = null;

        public Builder withInterval(int interval) {
            if (interval <= 0) {
                throw new IllegalArgumentException("Interval must be greater than 0");
            }
            this.interval = interval;
            return this;
        }

        public Builder withIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder withInfiniteIterations() {
            this.iterations = 0;
            return this;
        }

        public Builder withRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public Builder withConsumerTask(Consumer<Void> consumerTask) {
            this.consumerTask = consumerTask;
            return this;
        }

        public AsyncScheduler build() {
            if (interval <= 0) {
                throw new IllegalStateException("Interval must be set");
            }
            return new AsyncScheduler(interval, iterations, runnable, consumerTask);
        }
    }
}