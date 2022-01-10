/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.uuid;

import java.util.UUID;

public interface UUIDCache {
    public UUID uuid(String var1);

    public String name(UUID var1);

    public void ensure(UUID var1);

    public void update(UUID var1, String var2);
}

