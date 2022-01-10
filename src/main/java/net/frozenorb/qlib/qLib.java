/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketListener
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder
 *  org.bukkit.event.Event
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.BlockVector
 *  org.bukkit.util.Vector
 */
package net.frozenorb.qlib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.librato.metrics.DefaultHttpPoster;
import com.librato.metrics.HttpPoster;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.boss.FrozenBossBarHandler;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.economy.FrozenEconomyHandler;
import net.frozenorb.qlib.event.HalfHourEvent;
import net.frozenorb.qlib.event.HourEvent;
import net.frozenorb.qlib.hologram.HologramListener;
import net.frozenorb.qlib.librato.FastLibratoPostTask;
import net.frozenorb.qlib.librato.LibratoListener;
import net.frozenorb.qlib.librato.LibratoPostTask;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.protocol.InventoryAdapter;
import net.frozenorb.qlib.protocol.LagCheck;
import net.frozenorb.qlib.protocol.PingAdapter;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.serialization.BlockVectorAdapter;
import net.frozenorb.qlib.serialization.ItemStackAdapter;
import net.frozenorb.qlib.serialization.LocationAdapter;
import net.frozenorb.qlib.serialization.PotionEffectAdapter;
import net.frozenorb.qlib.serialization.VectorAdapter;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.tab.TabAdapter;
import net.frozenorb.qlib.util.ItemUtils;
import net.frozenorb.qlib.util.TPSUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import net.frozenorb.qlib.xpacket.FrozenXPacketHandler;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class qLib
extends JavaPlugin {
    private static qLib instance;
    private long localRedisLastError;
    private long backboneRedisLastError;
    public static boolean testing;
    public static final Random RANDOM;
    public static final Gson GSON;
    public static final Gson PLAIN_GSON;
    private JedisPool localJedisPool;
    private JedisPool backboneJedisPool;
    private HttpPoster libratoPoster;

    public void onEnable() {
        instance = this;
        testing = this.getConfig().getBoolean("testing", false);
        this.saveDefaultConfig();
        try {
            this.localJedisPool = new JedisPool((GenericObjectPoolConfig)new JedisPoolConfig(), this.getConfig().getString("Redis.Host"), 6379, 20000, null, this.getConfig().getInt("Redis.DbId", 0));
        }
        catch (Exception e) {
            this.localJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Redis instance at " + this.getConfig().getString("Redis.Host") + ".");
        }
        try {
            this.backboneJedisPool = new JedisPool((GenericObjectPoolConfig)new JedisPoolConfig(), this.getConfig().getString("BackboneRedis.Host"), 6379, 20000, null, this.getConfig().getInt("BackboneRedis.DbId", 0));
        }
        catch (Exception e) {
            this.backboneJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Backbone Redis instance at " + this.getConfig().getString("BackboneRedis.Host") + ".");
        }
        if (this.getConfig().getBoolean("Librato.Enabled", false)) {
            try {
                String libratoEmail = this.getConfig().getString("Librato.Email");
                String libratoToken = this.getConfig().getString("Librato.Token");
                String libratoUrl = "https://metrics-api.librato.com/v1/metrics";
                this.libratoPoster = new DefaultHttpPoster(libratoUrl, libratoEmail, libratoToken);
                new LibratoPostTask().runTaskTimerAsynchronously((Plugin)this, 200L, 1200L);
                new FastLibratoPostTask().runTaskTimerAsynchronously((Plugin)this, 200L, 100L);
                this.getServer().getPluginManager().registerEvents((Listener)new LibratoListener(), (Plugin)this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        FrozenCommandHandler.init();
        FrozenNametagHandler.init();
        FrozenScoreboardHandler.init();
        FrozenUUIDCache.init();
        FrozenXPacketHandler.init();
        FrozenBossBarHandler.init();
        FrozenTabHandler.init();
        AutoRebootHandler.init();
        FrozenVisibilityHandler.init();
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (BukkitRunnable)new TPSUtils(), 1L, 1L);
        ItemUtils.load();
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        new BukkitRunnable(){

            public void run() {
                if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new InventoryAdapter());
                    PingAdapter ping = new PingAdapter();
                    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)ping);
                    Bukkit.getPluginManager().registerEvents((Listener)ping, (Plugin)qLib.getInstance());
                    new LagCheck().runTaskTimerAsynchronously((Plugin)qLib.this, 100L, 100L);
                    ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new TabAdapter());
                    Bukkit.getPluginManager().registerEvents((Listener)new HologramListener(), (Plugin)qLib.this);
                }
            }
        }.runTaskLater((Plugin)this, 1L);
        this.setupHourEvents();
    }

    public void onDisable() {
        if (FrozenEconomyHandler.isInitiated()) {
            FrozenEconomyHandler.saveAll();
        }
        this.localJedisPool.close();
        this.backboneJedisPool.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        if (testing) {
            return null;
        }
        Jedis jedis = this.localJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.localRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.localJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
        if (testing) {
            return null;
        }
        Jedis jedis = this.backboneJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.backboneRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.backboneJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    @Deprecated
    public long getLocalRedisLastError() {
        return this.localRedisLastError;
    }

    @Deprecated
    public long getBackboneRedisLastError() {
        return this.backboneRedisLastError;
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("qLib - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(12);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = minToHour >= 30 ? minToHour : 30 - minOfHour;
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask((Plugin)this, () -> Bukkit.getPluginManager().callEvent((Event)new HourEvent(Calendar.getInstance().get(11)))), minToHour, 60L, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask((Plugin)this, () -> Bukkit.getPluginManager().callEvent((Event)new HalfHourEvent(Calendar.getInstance().get(11), Calendar.getInstance().get(12)))), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    public static qLib getInstance() {
        return instance;
    }

    public JedisPool getLocalJedisPool() {
        return this.localJedisPool;
    }

    public JedisPool getBackboneJedisPool() {
        return this.backboneJedisPool;
    }

    public HttpPoster getLibratoPoster() {
        return this.libratoPoster;
    }

    static {
        testing = false;
        RANDOM = new Random();
        GSON = new GsonBuilder().registerTypeHierarchyAdapter(PotionEffect.class, (Object)new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, (Object)new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, (Object)new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, (Object)new VectorAdapter()).registerTypeAdapter(BlockVector.class, (Object)new BlockVectorAdapter()).setPrettyPrinting().serializeNulls().create();
        PLAIN_GSON = new GsonBuilder().registerTypeHierarchyAdapter(PotionEffect.class, (Object)new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, (Object)new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, (Object)new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, (Object)new VectorAdapter()).registerTypeAdapter(BlockVector.class, (Object)new BlockVectorAdapter()).serializeNulls().create();
    }
}

