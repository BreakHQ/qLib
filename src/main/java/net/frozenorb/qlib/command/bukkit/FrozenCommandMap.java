/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandException
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.entity.Player
 *  org.bukkit.util.StringUtil
 */
package net.frozenorb.qlib.command.bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.frozenorb.qlib.command.CommandNode;
import net.frozenorb.qlib.command.bukkit.FrozenCommand;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class FrozenCommandMap
extends SimpleCommandMap {
    public FrozenCommandMap(Server server) {
        super(server);
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        Validate.notNull((Object)sender, (String)"Sender cannot be null");
        Validate.notNull((Object)cmdLine, (String)"Command line cannot null");
        int spaceIndex = cmdLine.indexOf(32);
        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<String>();
            Map knownCommands = this.knownCommands;
            String prefix = sender instanceof Player ? "/" : "";
            for (Map.Entry commandEntry : knownCommands.entrySet()) {
                String name = (String)commandEntry.getKey();
                if (!StringUtil.startsWithIgnoreCase((String)name, (String)cmdLine)) continue;
                Command command = (Command)commandEntry.getValue();
                if (command instanceof FrozenCommand) {
                    CommandNode executionNode = ((FrozenCommand)command).node.getCommand(name);
                    if (executionNode == null) {
                        executionNode = ((FrozenCommand)command).node;
                    }
                    if (!executionNode.hasCommands()) {
                        CommandNode testNode = executionNode.getCommand(name);
                        if (testNode == null) {
                            testNode = ((FrozenCommand)command).node.getCommand(name);
                        }
                        if (!testNode.canUse(sender)) continue;
                        completions.add(prefix + name);
                        continue;
                    }
                    if (executionNode.getSubCommands(sender, false).size() == 0) continue;
                    completions.add(prefix + name);
                    continue;
                }
                if (!command.testPermissionSilent(sender)) continue;
                completions.add(prefix + name);
            }
            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }
        String commandName = cmdLine.substring(0, spaceIndex);
        Command target = this.getCommand(commandName);
        if (target == null) {
            return null;
        }
        if (!target.testPermissionSilent(sender)) {
            return null;
        }
        String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
        String[] args = argLine.split(" ");
        try {
            List completions;
            List list = completions = target instanceof FrozenCommand ? ((FrozenCommand)target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args);
            if (completions != null) {
                Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            }
            return completions;
        }
        catch (CommandException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + (Object)target, ex);
        }
    }
}

