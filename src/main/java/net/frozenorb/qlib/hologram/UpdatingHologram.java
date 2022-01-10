/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.hologram;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.frozenorb.qlib.hologram.BaseHologram;
import net.frozenorb.qlib.hologram.Hologram;
import net.frozenorb.qlib.hologram.HologramLine;
import net.frozenorb.qlib.hologram.UpdatingHologramBuilder;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class UpdatingHologram
extends BaseHologram {
    private long interval = 1L;
    private Consumer<Hologram> updateFunction;
    private boolean showing = false;

    public UpdatingHologram(UpdatingHologramBuilder builder) {
        super(builder);
        this.interval = builder.getInterval();
        this.updateFunction = builder.getUpdateFunction();
    }

    @Override
    public void send() {
        if (this.showing) {
            this.update();
            return;
        }
        super.send();
        this.showing = true;
        new BukkitRunnable(){

            public void run() {
                if (!UpdatingHologram.this.showing) {
                    this.cancel();
                } else {
                    UpdatingHologram.this.update();
                }
            }
        }.runTaskTimerAsynchronously((Plugin)qLib.getInstance(), 0L, this.interval * 20L);
    }

    @Override
    public void setLine(int index, String line) {
        if (index > this.rawLines().size() - 1) {
            this.rawLines().add(new HologramLine(line));
        } else if (this.rawLines().get(index) != null) {
            this.rawLines().get(index).setText(line);
        } else {
            this.rawLines().set(index, new HologramLine(line));
        }
    }

    @Override
    public void setLines(Collection<String> lines) {
        Collection viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.destroy0(player);
        }
        this.rawLines().clear();
        for (String line : lines) {
            this.rawLines().add(new HologramLine(line));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.showing = false;
    }

    @Override
    public void update() {
        this.updateFunction.accept(this);
        if (!this.showing) {
            return;
        }
        Collection viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.update(player);
        }
        this.lastLines = this.lines;
    }
}

