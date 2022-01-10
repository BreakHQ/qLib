/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.nametag;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.nametag.NametagUpdate;

final class NametagThread
extends Thread {
    private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<NametagUpdate, Boolean>();

    public NametagThread() {
        super("qLib - Nametag Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();
            while (pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();
                try {
                    FrozenNametagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long)FrozenNametagHandler.getUpdateInterval() * 50L);
                continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }

    public static Map<NametagUpdate, Boolean> getPendingUpdates() {
        return pendingUpdates;
    }
}

