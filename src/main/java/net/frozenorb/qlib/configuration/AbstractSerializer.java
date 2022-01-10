/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.configuration;

public abstract class AbstractSerializer<T> {
    public abstract String toString(T var1);

    public abstract T fromString(String var1);
}

