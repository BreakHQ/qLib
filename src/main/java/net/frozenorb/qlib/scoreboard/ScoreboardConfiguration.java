/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.scoreboard.TitleGetter;

public final class ScoreboardConfiguration {
    private TitleGetter titleGetter;
    private ScoreGetter scoreGetter;

    public TitleGetter getTitleGetter() {
        return this.titleGetter;
    }

    public void setTitleGetter(TitleGetter titleGetter) {
        this.titleGetter = titleGetter;
    }

    public ScoreGetter getScoreGetter() {
        return this.scoreGetter;
    }

    public void setScoreGetter(ScoreGetter scoreGetter) {
        this.scoreGetter = scoreGetter;
    }
}

