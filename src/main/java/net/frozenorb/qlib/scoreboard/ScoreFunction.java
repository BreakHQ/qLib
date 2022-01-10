/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.scoreboard;

import net.frozenorb.qlib.util.TimeUtils;

public interface ScoreFunction<T> {
    public static final ScoreFunction<Float> TIME_FANCY = value -> {
        if (value.floatValue() >= 60.0f) {
            return TimeUtils.formatIntoMMSS(value.intValue());
        }
        return (double)Math.round(10.0 * (double)value.floatValue()) / 10.0 + "s";
    };
    public static final ScoreFunction<Float> TIME_SIMPLE = value -> TimeUtils.formatIntoMMSS(value.intValue());

    public String apply(T var1);
}

