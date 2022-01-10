/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package net.frozenorb.qlib.border;

import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.BorderTask;
import net.frozenorb.qlib.border.event.BorderChangeEvent;
import net.frozenorb.qlib.border.event.PlayerEnterBorderEvent;
import net.frozenorb.qlib.border.event.PlayerExitBorderEvent;
import net.frozenorb.qlib.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExampleBorderListener
implements Listener {
    @EventHandler
    public void onBorderChange(BorderChangeEvent event) {
        Bukkit.broadcastMessage((String)ChatColor.translateAlternateColorCodes((char)'&', (String)("&6&lBorder size has " + (event.getAction() == BorderTask.BorderAction.SHRINK ? "&cdecreased" : "&aincreased&c&l!"))));
        if (event.getBorder().getPhysicalBounds().getSizeX() <= 500 && event.getBorder().getPhysicalBounds().getSizeZ() <= 500) {
            event.getBorder().getBorderTask().setAction(BorderTask.BorderAction.NONE);
        }
    }

    @EventHandler
    public void onBorderExit(PlayerExitBorderEvent event) {
        event.getPlayer().sendMessage((Object)ChatColor.RED + "Warning! You have left the border!");
        Location playerLocation = event.getPlayer().getLocation();
        Border border = event.getBorder();
        Cuboid cuboid = border.getPhysicalBounds();
        int minX = cuboid.getLowerX();
        int minZ = cuboid.getLowerZ();
        int maxX = cuboid.getUpperX();
        int maxZ = cuboid.getUpperZ();
        int newX = playerLocation.getBlockX();
        int newZ = playerLocation.getBlockZ();
        if (maxX < playerLocation.getBlockX()) {
            newX = maxX;
        } else if (minX > playerLocation.getBlockX()) {
            newX = minX;
        }
        if (maxZ < playerLocation.getBlockZ()) {
            newZ = maxZ;
        } else if (minZ > playerLocation.getBlockZ()) {
            newZ = minZ;
        }
        event.getPlayer().teleport(new Location(playerLocation.getWorld(), (double)newX, playerLocation.getY(), (double)newZ));
    }

    @EventHandler
    public void onBorderEnter(PlayerEnterBorderEvent event) {
        event.getPlayer().sendMessage((Object)ChatColor.GREEN + "You have entered the border");
    }
}

