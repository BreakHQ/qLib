/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.scoreboard;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleGetter {
    private String defaultTitle;

    @Deprecated
    public TitleGetter(String defaultTitle) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes((char)'&', (String)defaultTitle);
    }

    public TitleGetter() {
    }

    public static TitleGetter forStaticString(final String staticString) {
        Preconditions.checkNotNull((Object)staticString);
        return new TitleGetter(){

            @Override
            public String getTitle(Player player) {
                return staticString;
            }
        };
    }

    public String getTitle(Player player) {
        return this.defaultTitle;
    }
}

