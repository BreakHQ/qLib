/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 */
package net.frozenorb.qlib.serialization;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.qlib.qLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerInventorySerializer {
    public static String serialize(Player player) {
        return qLib.PLAIN_GSON.toJson((Object)new PlayerInventoryWrapper(player));
    }

    public static PlayerInventoryWrapper deserialize(String json) {
        return (PlayerInventoryWrapper)qLib.PLAIN_GSON.fromJson(json, PlayerInventoryWrapper.class);
    }

    public static BasicDBObject getInsertableObject(Player player) {
        return (BasicDBObject)JSON.parse(PlayerInventorySerializer.serialize(player));
    }

    public static class PlayerInventoryWrapper {
        private final PotionEffect[] effects;
        private final ItemStack[] contents;
        private final ItemStack[] armor;
        private final int health;
        private final int hunger;

        public PlayerInventoryWrapper(Player player) {
            ItemStack stack;
            int i;
            this.contents = player.getInventory().getContents();
            for (i = 0; i < this.contents.length; ++i) {
                stack = this.contents[i];
                if (stack != null) continue;
                this.contents[i] = new ItemStack(Material.AIR, 0, 0);
            }
            this.armor = player.getInventory().getArmorContents();
            for (i = 0; i < this.armor.length; ++i) {
                stack = this.armor[i];
                if (stack != null) continue;
                this.armor[i] = new ItemStack(Material.AIR, 0, 0);
            }
            this.effects = player.getActivePotionEffects().toArray((T[])new PotionEffect[player.getActivePotionEffects().size()]);
            this.health = (int)player.getHealth();
            this.hunger = player.getFoodLevel();
        }

        public void apply(Player player) {
            player.getInventory().setContents(this.contents);
            player.getInventory().setArmorContents(this.armor);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            for (PotionEffect effect : this.effects) {
                player.addPotionEffect(effect);
            }
        }

        public PotionEffect[] getEffects() {
            return this.effects;
        }

        public ItemStack[] getContents() {
            return this.contents;
        }

        public ItemStack[] getArmor() {
            return this.armor;
        }

        public int getHealth() {
            return this.health;
        }

        public int getHunger() {
            return this.hunger;
        }
    }
}

