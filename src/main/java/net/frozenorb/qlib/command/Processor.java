/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command;

@FunctionalInterface
public interface Processor<T, R> {
    public R process(T var1);
}

