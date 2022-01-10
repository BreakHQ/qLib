/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.frozenorb.qlib.qLib;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class BungeeUtils {
    private BungeeUtils() {
    }

    public static void send(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        player.sendPluginMessage((Plugin)qLib.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void sendAll(String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
            player.sendPluginMessage((Plugin)qLib.getInstance(), "BungeeCord", b.toByteArray());
        }
    }
}

