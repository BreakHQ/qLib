/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.hologram;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.frozenorb.qlib.hologram.Hologram;
import net.frozenorb.qlib.hologram.HologramBuilder;
import net.frozenorb.qlib.hologram.UpdatingHologram;

public final class UpdatingHologramBuilder
extends HologramBuilder {
    private long interval;
    private Consumer<Hologram> updateFunction;

    protected UpdatingHologramBuilder(HologramBuilder hologramBuilder) {
        super(hologramBuilder.getViewers());
        this.lines = hologramBuilder.getLines();
        this.at(hologramBuilder.getLocation());
    }

    public UpdatingHologramBuilder interval(long time, TimeUnit unit) {
        this.interval = unit.toSeconds(time);
        return this;
    }

    public UpdatingHologramBuilder onUpdate(Consumer<Hologram> onUpdate) {
        this.updateFunction = onUpdate;
        return this;
    }

    @Override
    public Hologram build() {
        return new UpdatingHologram(this);
    }

    protected long getInterval() {
        return this.interval;
    }

    protected Consumer<Hologram> getUpdateFunction() {
        return this.updateFunction;
    }
}

