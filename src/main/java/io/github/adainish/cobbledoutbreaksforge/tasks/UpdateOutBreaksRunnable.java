package io.github.adainish.cobbledoutbreaksforge.tasks;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaksForge;
import io.github.adainish.cobbledoutbreaksforge.obj.OutbreaksManager;

public class UpdateOutBreaksRunnable implements Runnable{

    @Override
    public void run() {

        if (CobbledOutBreaksForge.getServer() != null)
        {
            OutbreaksManager manager = CobbledOutBreaksForge.outbreaksManager;
            if (manager != null) {
                manager.cleanExpiredOutBreaks();
                manager.generateOutBreaks();
            } else {
                CobbledOutBreaksForge.getLog().error("Failed to load the outbreak manager");
            }
        } else {
            CobbledOutBreaksForge.getLog().error("Failed to update outbreak data, server instance null");
        }
    }
}
