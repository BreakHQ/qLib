/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo
 *  net.minecraft.util.com.mojang.authlib.GameProfile
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.tab;

import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PlayerInfoPacketMod {
    private PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

    public PlayerInfoPacketMod(String name, int ping, GameProfile profile, int action) {
        this.setField("username", name);
        this.setField("ping", ping);
        this.setField("action", action);
        this.setField("player", (Object)profile);
    }

    public void setField(String field, Object value) {
        try {
            Field fieldObject = this.packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set((Object)this.packet, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToPlayer(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)this.packet);
    }
}

