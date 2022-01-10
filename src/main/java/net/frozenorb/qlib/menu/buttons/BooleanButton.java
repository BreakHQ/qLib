/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 */
package net.frozenorb.qlib.menu.buttons;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BooleanButton
extends Button {
    private boolean confirm;
    private Callback<Boolean> callback;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }
        player.closeInventory();
        this.callback.callback(this.confirm);
    }

    @Override
    public String getName(Player player) {
        return this.confirm ? "\u00a7aConfirm" : "\u00a7cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<String>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return this.confirm ? (byte)5 : 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @ConstructorProperties(value={"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback) {
        this.confirm = confirm;
        this.callback = callback;
    }
}

