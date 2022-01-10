/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.menu.pagination;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qlib.menu.buttons.BackButton;
import net.frozenorb.qlib.menu.pagination.JumpToPageButton;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

public class ViewAllPagesMenu
extends Menu {
    @NonNull
    PaginatedMenu menu;

    @Override
    public String getTitle(Player player) {
        return "Jump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        buttons.put(0, new BackButton(this.menu));
        int index = 10;
        for (int i = 1; i <= this.menu.getPages(player); ++i) {
            buttons.put(index++, new JumpToPageButton(i, this.menu));
            if ((index - 8) % 9 != 0) continue;
            index += 2;
        }
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @ConstructorProperties(value={"menu"})
    public ViewAllPagesMenu(@NonNull PaginatedMenu menu) {
        if (menu == null) {
            throw new NullPointerException("menu");
        }
        this.menu = menu;
    }

    @NonNull
    public PaginatedMenu getMenu() {
        return this.menu;
    }
}

