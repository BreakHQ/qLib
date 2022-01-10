/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.nametag;

import java.util.ArrayList;
import net.frozenorb.qlib.packet.ScoreboardTeamPacketMod;

public final class NametagInfo {
    private String name;
    private String prefix;
    private String suffix;
    private ScoreboardTeamPacketMod teamAddPacket;

    protected NametagInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList(), 0);
    }

    public boolean equals(Object other) {
        if (other instanceof NametagInfo) {
            NametagInfo otherNametag = (NametagInfo)other;
            return this.name.equals(otherNametag.name) && this.prefix.equals(otherNametag.prefix) && this.suffix.equals(otherNametag.suffix);
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public ScoreboardTeamPacketMod getTeamAddPacket() {
        return this.teamAddPacket;
    }
}

