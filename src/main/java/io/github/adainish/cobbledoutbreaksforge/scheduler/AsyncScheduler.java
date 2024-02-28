package io.github.adainish.cobbledoutbreaksforge.scheduler;

import java.util.function.Consumer;


import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import com.cobblemon.mod.common.platform.events.ServerTickEvent;
import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaks;
import kotlin.Unit;

public class AsyncScheduler {

    private final int interval;
    private final int iterations;
    private final Runnable runnable;
    private final Consumer<Void> consumerTask;
    private int iterationCount = 0;
    private boolean expired = false;
    private int ticksElapsed = 0;

    public ObservableSubscription<ServerTickEvent> subscription;


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
        CobbledOutBreaks.getLog().debug("Starting async scheduler with interval of {} ticks and {} iterations", interval, iterations);
        PlatformEvents.SERVER_TICK_PRE.subscribe(Priority.HIGHEST, t -> {
            onServerTick();
            return Unit.INSTANCE;
        });
    }

    public void stop() {
        expired = true;
        if (subscription != null) {
            subscription.unsubscribe();
        } else {
            CobbledOutBreaks.getLog().warn("Attempted to stop async scheduler but no subscription was found");
        }
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

    public void onServerTick() {
            if (expired) {
                return;
            }
            ticksElapsed++;
            if (ticksElapsed >= interval) {
                ticksElapsed = 0;
                execute();
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