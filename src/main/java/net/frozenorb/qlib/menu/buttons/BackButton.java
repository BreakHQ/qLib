/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.qlib.menu.buttons;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BackButton
extends Button {
    private Menu back;

    @Override
    public Material getMaterial(Player player) {
        return Material.BED;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public String getName(Player player) {
        return "\u00a7cGo back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<String>();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }

    @ConstructorProperties(value={"back"})
    public BackButton(Menu back) {
        this.back = back;
    }
}

