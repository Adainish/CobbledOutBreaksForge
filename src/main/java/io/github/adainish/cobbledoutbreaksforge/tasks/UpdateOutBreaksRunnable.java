package io.github.adainish.cobbledoutbreaksforge.tasks;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.obj.OutbreaksManager;

public class UpdateOutBreaksRunnable implements Runnable{
    @Override
    public void run() {

        if (CobbledOutBreaksForge.getServer() != null)
        {
            if (CobbledOutBreaksForge.getServer().getPlayerCount() <= 0)
                return;
            OutbreaksManager manager = CobbledOutBreaksForge.outbreaksManager;
            if (manager != null) {
                manager.cleanExpiredOutBreaks();
                manager.generateOutBreaks();
            }
        }
    }
}
