/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package net.frozenorb.qlib.autoreboot.listeners;

import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.event.HourEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoRebootListener
implements Listener {
    @EventHandler
    public void onHour(HourEvent event) {
        if (AutoRebootHandler.getRebootTimes().contains(event.getHour())) {
            AutoRebootHandler.rebootServer(5, TimeUnit.MINUTES);
        }
    }
}

