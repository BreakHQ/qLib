/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.border;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.frozenorb.qlib.border.DefaultBorderActions;
import net.frozenorb.qlib.border.event.BorderChangeEvent;
import net.frozenorb.qlib.border.event.PlayerEnterBorderEvent;
import net.frozenorb.qlib.border.event.PlayerExitBorderEvent;

public final class BorderConfiguration {
    public static final BorderConfiguration DEFAULT_CONFIGURATION = new BorderConfiguration();
    private final Set<Consumer<BorderChangeEvent>> defaultBorderChangeActions = new HashSet<Consumer<BorderChangeEvent>>();
    private final Set<Consumer<PlayerEnterBorderEvent>> defaultBorderEnterActions = new HashSet<Consumer<PlayerEnterBorderEvent>>();
    private final Set<Consumer<PlayerExitBorderEvent>> defaultBorderExitActions = new HashSet<Consumer<PlayerExitBorderEvent>>();

    public BorderConfiguration() {
        this.defaultBorderChangeActions.add(DefaultBorderActions.ENSURE_PLAYERS_IN_BORDER);
        this.defaultBorderExitActions.add(DefaultBorderActions.PUSHBACK_ON_EXIT);
        this.defaultBorderExitActions.add(DefaultBorderActions.CANCEL_EXIT);
    }

    public Set<Consumer<BorderChangeEvent>> getDefaultBorderChangeActions() {
        return this.defaultBorderChangeActions;
    }

    public Set<Consumer<PlayerEnterBorderEvent>> getDefaultBorderEnterActions() {
        return this.defaultBorderEnterActions;
    }

    public Set<Consumer<PlayerExitBorderEvent>> getDefaultBorderExitActions() {
        return this.defaultBorderExitActions;
    }
}

