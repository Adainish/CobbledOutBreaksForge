package io.github.adainish.cobbledoutbreaksforge.tasks;

import io.github.adainish.cobbledoutbreaksforge.CobbledOutBreaks;
import io.github.adainish.cobbledoutbreaksforge.obj.OutbreaksManager;

public class UpdateOutBreaksRunnable implements Runnable{

    @Override
    public void run() {

        if (CobbledOutBreaks.getServer() != null)
        {
            OutbreaksManager manager = CobbledOutBreaks.outbreaksManager;
            if (manager != null) {
                manager.cleanExpiredOutBreaks();
                manager.generateOutBreaks();
            } else {
                CobbledOutBreaks.getLog().error("Failed to load the outbreak manager");
            }
        } else {
            CobbledOutBreaks.getLog().error("Failed to update outbreak data, server instance null");
        }
    }
}
