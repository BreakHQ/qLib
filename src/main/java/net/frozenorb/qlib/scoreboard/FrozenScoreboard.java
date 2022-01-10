/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore
 *  org.bukkit.ChatColor
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 */
package net.frozenorb.qlib.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.frozenorb.qlib.packet.ScoreboardTeamPacketMod;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.util.LinkedList;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

final class FrozenScoreboard {
    private Player player;
    private Objective objective;
    private Map<String, Integer> displayedScores = new HashMap<String, Integer>();
    private Map<String, String> scorePrefixes = new HashMap<String, String>();
    private Map<String, String> scoreSuffixes = new HashMap<String, String>();
    private Set<String> sentTeamCreates = new HashSet<String>();
    private final StringBuilder separateScoreBuilder = new StringBuilder();
    private final List<String> separateScores = new ArrayList<String>();
    private final Set<String> recentlyUpdatedScores = new HashSet<String>();
    private final Set<String> usedBaseScores = new HashSet<String>();
    private final String[] prefixScoreSuffix = new String[3];
    private final ThreadLocal<LinkedList<String>> localList = ThreadLocal.withInitial(LinkedList::new);

    public FrozenScoreboard(Player player) {
        this.player = player;
        Scoreboard board = qLib.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective("VeltPvP", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
    }

    public void update() {
        String untranslatedTitle = FrozenScoreboardHandler.getConfiguration().getTitleGetter().getTitle(this.player);
        String title = ChatColor.translateAlternateColorCodes((char)'&', (String)untranslatedTitle);
        List lines = this.localList.get();
        if (!lines.isEmpty()) {
            lines.clear();
        }
        FrozenScoreboardHandler.getConfiguration().getScoreGetter().getScores(this.localList.get(), this.player);
        this.recentlyUpdatedScores.clear();
        this.usedBaseScores.clear();
        int nextValue = lines.size();
        Preconditions.checkArgument((lines.size() < 16 ? 1 : 0) != 0, (Object)"Too many lines passed!");
        Preconditions.checkArgument((title.length() < 32 ? 1 : 0) != 0, (Object)"Title is too long!");
        if (!this.objective.getDisplayName().equals(title)) {
            this.objective.setDisplayName(title);
        }
        for (String line : lines) {
            if (48 <= line.length()) {
                throw new IllegalArgumentException("Line is too long! Offending line: " + line);
            }
            String[] separated = this.separate(line, this.usedBaseScores);
            String prefix = separated[0];
            String score = separated[1];
            String suffix = separated[2];
            this.recentlyUpdatedScores.add(score);
            if (!this.sentTeamCreates.contains(score)) {
                this.createAndAddMember(score);
            }
            if (!this.displayedScores.containsKey(score) || this.displayedScores.get(score) != nextValue) {
                this.setScore(score, nextValue);
            }
            if (!(this.scorePrefixes.containsKey(score) && this.scorePrefixes.get(score).equals(prefix) && this.scoreSuffixes.get(score).equals(suffix))) {
                this.updateScore(score, prefix, suffix);
            }
            --nextValue;
        }
        for (String displayedScore : ImmutableSet.copyOf(this.displayedScores.keySet())) {
            if (this.recentlyUpdatedScores.contains(displayedScore)) continue;
            this.removeScore(displayedScore);
        }
    }

    private void setField(Packet packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set((Object)packet, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddMember(String scoreTitle) {
        ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod(scoreTitle, "_", "_", (Collection)ImmutableList.of(), 0);
        ScoreboardTeamPacketMod scoreboardTeamAddMember = new ScoreboardTeamPacketMod(scoreTitle, (Collection)ImmutableList.of((Object)scoreTitle), 3);
        scoreboardTeamAdd.sendToPlayer(this.player);
        scoreboardTeamAddMember.sendToPlayer(this.player);
        this.sentTeamCreates.add(scoreTitle);
    }

    private void setScore(String score, int value) {
        PacketPlayOutScoreboardScore scoreboardScorePacket = new PacketPlayOutScoreboardScore();
        this.setField((Packet)scoreboardScorePacket, "a", score);
        this.setField((Packet)scoreboardScorePacket, "b", this.objective.getName());
        this.setField((Packet)scoreboardScorePacket, "c", value);
        this.setField((Packet)scoreboardScorePacket, "d", 0);
        this.displayedScores.put(score, value);
        ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket((Packet)scoreboardScorePacket);
    }

    private void removeScore(String score) {
        this.displayedScores.remove(score);
        this.scorePrefixes.remove(score);
        this.scoreSuffixes.remove(score);
        ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore(score));
    }

    private void updateScore(String score, String prefix, String suffix) {
        this.scorePrefixes.put(score, prefix);
        this.scoreSuffixes.put(score, suffix);
        new ScoreboardTeamPacketMod(score, prefix, suffix, null, 2).sendToPlayer(this.player);
    }

    private String[] separate(String line, Collection<String> usedBaseScores) {
        line = ChatColor.translateAlternateColorCodes((char)'&', (String)line);
        String prefix = "";
        String score = "";
        String suffix = "";
        this.separateScores.clear();
        this.separateScoreBuilder.setLength(0);
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (c == '*' || this.separateScoreBuilder.length() == 16 && this.separateScores.size() < 3) {
                this.separateScores.add(this.separateScoreBuilder.toString());
                this.separateScoreBuilder.setLength(0);
                if (c == '*') continue;
            }
            this.separateScoreBuilder.append(c);
        }
        this.separateScores.add(this.separateScoreBuilder.toString());
        switch (this.separateScores.size()) {
            case 1: {
                score = this.separateScores.get(0);
                break;
            }
            case 2: {
                score = this.separateScores.get(0);
                suffix = this.separateScores.get(1);
                break;
            }
            case 3: {
                prefix = this.separateScores.get(0);
                score = this.separateScores.get(1);
                suffix = this.separateScores.get(2);
                break;
            }
            default: {
                qLib.getInstance().getLogger().warning("Failed to separate scoreboard line. Input: " + line);
            }
        }
        if (usedBaseScores.contains(score)) {
            if (score.length() <= 14) {
                for (ChatColor chatColor : ChatColor.values()) {
                    String possibleScore = (Object)chatColor + score;
                    if (usedBaseScores.contains(possibleScore)) continue;
                    score = possibleScore;
                    break;
                }
                if (usedBaseScores.contains(score)) {
                    qLib.getInstance().getLogger().warning("Failed to find alternate color code for: " + score);
                }
            } else {
                qLib.getInstance().getLogger().warning("Found a scoreboard base collision to shift: " + score);
            }
        }
        if (prefix.length() > 16) {
            prefix = ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + ">16";
        }
        if (score.length() > 16) {
            score = ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + ">16";
        }
        if (suffix.length() > 16) {
            suffix = ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + ">16";
        }
        usedBaseScores.add(score);
        this.prefixScoreSuffix[0] = prefix;
        this.prefixScoreSuffix[1] = score;
        this.prefixScoreSuffix[2] = suffix;
        return this.prefixScoreSuffix;
    }
}

