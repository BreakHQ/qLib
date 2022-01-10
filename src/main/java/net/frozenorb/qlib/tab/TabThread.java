/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.tab;

import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread
extends Thread {
    private Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");

    public TabThread() {
        this.setName("qLib - Tab Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (qLib.getInstance().isEnabled() && this.protocolLib != null && this.protocolLib.isEnabled()) {
            for (Player online : qLib.getInstance().getServer().getOnlinePlayers()) {
                try {
                    FrozenTabHandler.updatePlayer(online);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(250L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

