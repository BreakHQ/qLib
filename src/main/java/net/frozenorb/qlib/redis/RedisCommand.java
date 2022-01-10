/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.redis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
    public T execute(Jedis var1);
}

