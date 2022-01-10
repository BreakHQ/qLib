/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package net.frozenorb.qlib.border;

import net.frozenorb.qlib.border.event.BorderChangeEvent;
import net.frozenorb.qlib.border.event.PlayerEnterBorderEvent;
import net.frozenorb.qlib.border.event.PlayerExitBorderEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InternalBorderListener
implements Listener {
    @EventHandler
    public void onBorderChange(BorderChangeEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderChangeActions().forEach(c -> c.accept(event));
    }

    @EventHandler
    public void onBorderExit(PlayerExitBorderEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderExitActions().forEach(c -> c.accept(event));
    }

    @EventHandler
    public void onBorderEnter(PlayerEnterBorderEvent event) {
        event.getBorder().getBorderConfiguration().getDefaultBorderEnterActions().forEach(c -> c.accept(event));
    }
}

