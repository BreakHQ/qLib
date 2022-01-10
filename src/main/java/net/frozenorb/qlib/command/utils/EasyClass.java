/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command.utils;

import net.frozenorb.qlib.command.utils.EasyField;
import net.frozenorb.qlib.command.utils.EasyMethod;

public class EasyClass<T> {
    private Class<T> clazz;
    private T object;

    public EasyClass(T object) {
        if (object != null) {
            this.clazz = object.getClass();
        }
        this.object = object;
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public T get() {
        return this.object;
    }

    public EasyMethod getMethod(String name, Object ... parameters) {
        return new EasyMethod(this, name, parameters);
    }

    public <ST> EasyField<ST> getField(String name) {
        return new EasyField(this, name);
    }
}

