/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 */
package net.frozenorb.qlib.border.event;

import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.event.BorderEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerBorderEvent
extends BorderEvent
implements Cancellable {
    private Player player;
    private boolean cancelled = false;

    public PlayerBorderEvent(Border border, Player player) {
        super(border);
        this.player = player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return this.player;
    }
}

