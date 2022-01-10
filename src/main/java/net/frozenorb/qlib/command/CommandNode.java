/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  lombok.NonNull
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandException
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 *  org.spigotmc.SpigotConfig
 */
package net.frozenorb.qlib.command;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.beans.ConstructorProperties;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.NonNull;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.command.Arguments;
import net.frozenorb.qlib.command.Data;
import net.frozenorb.qlib.command.FlagData;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.ParameterData;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

public class CommandNode {
    @NonNull
    private String name;
    private Set<String> aliases = new HashSet<String>();
    @NonNull
    private String permission;
    private String description;
    private boolean async;
    private boolean hidden;
    protected Method method;
    protected Class<?> owningClass;
    private List<String> validFlags;
    private List<Data> parameters;
    private Map<String, CommandNode> children = new TreeMap<String, CommandNode>();
    private CommandNode parent;
    private boolean logToConsole;

    public CommandNode(Class<?> owningClass) {
        this.owningClass = owningClass;
    }

    public void registerCommand(CommandNode commandNode) {
        commandNode.setParent(this);
        this.children.put(commandNode.getName(), commandNode);
    }

    public boolean hasCommand(String name) {
        return this.children.containsKey(name.toLowerCase());
    }

    public CommandNode getCommand(String name) {
        return this.children.get(name.toLowerCase());
    }

    public boolean hasCommands() {
        return this.children.size() > 0;
    }

    public CommandNode findCommand(Arguments arguments) {
        String trySub;
        if (arguments.getArguments().size() > 0 && this.hasCommand(trySub = arguments.getArguments().get(0))) {
            arguments.getArguments().remove(0);
            CommandNode returnNode = this.getCommand(trySub);
            return returnNode.findCommand(arguments);
        }
        return this;
    }

    public boolean isValidFlag(String test) {
        if (test.length() == 1) {
            return this.validFlags.contains(test);
        }
        return this.validFlags.contains(test.toLowerCase());
    }

    public boolean canUse(CommandSender sender) {
        if (this.permission == null) {
            return true;
        }
        switch (this.permission) {
            case "console": {
                return sender instanceof ConsoleCommandSender;
            }
            case "op": {
                return sender.isOp();
            }
            case "": {
                return true;
            }
        }
        return sender.hasPermission(this.permission);
    }

    public FancyMessage getUsage(String realLabel) {
        FancyMessage usage = new FancyMessage("Usage: /" + realLabel).color(ChatColor.RED);
        if (!Strings.isNullOrEmpty((String)this.getDescription())) {
            usage.tooltip((Object)ChatColor.YELLOW + this.getDescription());
        }
        ArrayList flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        ArrayList parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty((String)this.getDescription())) {
                usage.tooltip((Object)ChatColor.YELLOW + this.getDescription());
            }
            for (Data data2 : flags) {
                String name = ((FlagData)data2).getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                    if (!Strings.isNullOrEmpty((String)this.getDescription())) {
                        usage.tooltip((Object)ChatColor.YELLOW + this.getDescription());
                    }
                }
                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (Strings.isNullOrEmpty((String)((FlagData)data2).getDescription())) continue;
                usage.tooltip((Object)ChatColor.GRAY + ((FlagData)data2).getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty((String)this.getDescription())) {
                usage.tooltip((Object)ChatColor.YELLOW + this.getDescription());
            }
        }
        if (!parameters.isEmpty()) {
            for (int index = 0; index < parameters.size(); ++index) {
                Data data2;
                data2 = (ParameterData)parameters.get(index);
                boolean required = ((ParameterData)data2).getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + ((ParameterData)data2).getName() + (((ParameterData)data2).isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
                if (Strings.isNullOrEmpty((String)this.getDescription())) continue;
                usage.tooltip((Object)ChatColor.YELLOW + this.getDescription());
            }
        }
        return usage;
    }

    public FancyMessage getUsage() {
        FancyMessage usage = new FancyMessage("");
        ArrayList flags = Lists.newArrayList();
        flags.addAll(this.parameters.stream().filter(data -> data instanceof FlagData).map(data -> (FlagData)data).collect(Collectors.toList()));
        ArrayList parameters = Lists.newArrayList();
        parameters.addAll(this.parameters.stream().filter(data -> data instanceof ParameterData).map(data -> (ParameterData)data).collect(Collectors.toList()));
        boolean flagFirst = true;
        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);
            for (Data data2 : flags) {
                String name = ((FlagData)data2).getNames().get(0);
                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                }
                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);
                if (Strings.isNullOrEmpty((String)((FlagData)data2).getDescription())) continue;
                usage.tooltip((Object)ChatColor.GRAY + ((FlagData)data2).getDescription());
            }
            usage.then(") ").color(ChatColor.RED);
        }
        if (!parameters.isEmpty()) {
            for (int index = 0; index < parameters.size(); ++index) {
                Data data2;
                data2 = (ParameterData)parameters.get(index);
                boolean required = ((ParameterData)data2).getDefaultValue().isEmpty();
                usage.then((required ? "<" : "[") + ((ParameterData)data2).getName() + (((ParameterData)data2).isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
            }
        }
        return usage;
    }

    public boolean invoke(CommandSender sender, Arguments arguments) throws CommandException {
        if (this.method == null) {
            if (this.hasCommands()) {
                if (this.getSubCommands(sender, true).isEmpty()) {
                    if (this.isHidden()) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                    } else {
                        sender.sendMessage((Object)ChatColor.RED + "No permission.");
                    }
                }
            } else {
                sender.sendMessage(SpigotConfig.unknownCommandMessage);
            }
            return true;
        }
        ArrayList<Object> objects = new ArrayList<Object>(this.method.getParameterCount());
        objects.add((Object)sender);
        int index = 0;
        for (Data unknownData : this.parameters) {
            String argument;
            if (unknownData instanceof FlagData) {
                FlagData flagData = (FlagData)unknownData;
                boolean value = flagData.getDefaultValue();
                for (String s : flagData.getNames()) {
                    if (!arguments.hasFlag(s)) continue;
                    value = !value;
                    break;
                }
                objects.add(flagData.getMethodIndex(), value);
                continue;
            }
            if (!(unknownData instanceof ParameterData)) continue;
            ParameterData parameterData = (ParameterData)unknownData;
            try {
                argument = arguments.getArguments().get(index);
            }
            catch (Exception ex) {
                if (!parameterData.getDefaultValue().isEmpty()) {
                    argument = parameterData.getDefaultValue();
                }
                return false;
            }
            if (parameterData.isWildcard() && (argument.isEmpty() || !argument.equals(parameterData.getDefaultValue()))) {
                argument = arguments.join(index);
            }
            ParameterType type = FrozenCommandHandler.getParameterType(parameterData.getType());
            if (parameterData.getParameterType() != null) {
                try {
                    type = parameterData.getParameterType().newInstance();
                }
                catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    throw new CommandException("Failed to create ParameterType instance: " + parameterData.getParameterType().getName(), (Throwable)e);
                }
            }
            if (type == null) {
                Class<Object> t = parameterData.getParameterType() == null ? parameterData.getType() : parameterData.getParameterType();
                sender.sendMessage((Object)ChatColor.RED + "No parameter type found: " + t.getSimpleName());
                return true;
            }
            Object result = type.transform(sender, argument);
            if (result == null) {
                return true;
            }
            objects.add(parameterData.getMethodIndex(), result);
            ++index;
        }
        try {
            Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();
            this.method.invoke(null, objects.toArray());
            stopwatch.stop();
            int executionThreshold = qLib.getInstance().getConfig().getInt("Command.TimeThreshold", 10);
            if (!this.async && this.logToConsole && stopwatch.elapsedMillis() >= (long)executionThreshold) {
                qLib.getInstance().getLogger().warning("Command '/" + this.getFullLabel() + "' took " + stopwatch.elapsedMillis() + "ms!");
            }
            return true;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new CommandException("An error occurred while executing the command", (Throwable)e);
        }
    }

    public List<String> getSubCommands(CommandSender sender, boolean print) {
        ArrayList<String> commands = new ArrayList<String>();
        if (this.canUse(sender)) {
            String command = (sender instanceof Player ? "/" : "") + this.getFullLabel() + (this.parameters != null ? " " + this.getUsage().toOldMessageFormat() : "") + (!Strings.isNullOrEmpty((String)this.description) ? (Object)ChatColor.GRAY + " - " + this.getDescription() : "");
            if (this.parent == null) {
                commands.add(command);
            } else if (this.parent.getName() != null && FrozenCommandHandler.ROOT_NODE.getCommand(this.parent.getName()) != this.parent) {
                commands.add(command);
            }
            if (this.hasCommands()) {
                for (CommandNode n : this.getChildren().values()) {
                    commands.addAll(n.getSubCommands(sender, false));
                }
            }
        }
        if (!commands.isEmpty() && print) {
            sender.sendMessage(ChatColor.BLUE.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)35));
            for (String command : commands) {
                sender.sendMessage((Object)ChatColor.RED + command);
            }
            sender.sendMessage(ChatColor.BLUE.toString() + (Object)ChatColor.STRIKETHROUGH + StringUtils.repeat((char)'-', (int)35));
        }
        return commands;
    }

    public Set<String> getRealAliases() {
        Set<String> aliases = this.getAliases();
        aliases.remove(this.getName());
        return aliases;
    }

    public String getFullLabel() {
        ArrayList<String> labels = new ArrayList<String>();
        for (CommandNode node = this; node != null; node = node.getParent()) {
            String name = node.getName();
            if (name == null) continue;
            labels.add(name);
        }
        Collections.reverse(labels);
        labels.remove(0);
        StringBuilder builder = new StringBuilder();
        labels.forEach(s -> builder.append((String)s).append(' '));
        return builder.toString().trim();
    }

    public String getUsageForHelpTopic() {
        if (this.method != null && this.parameters != null) {
            return "/" + this.getFullLabel() + " " + ChatColor.stripColor((String)this.getUsage().toOldMessageFormat());
        }
        return "";
    }

    @ConstructorProperties(value={"name", "permission"})
    public CommandNode(@NonNull String name, @NonNull String permission) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (permission == null) {
            throw new NullPointerException("permission");
        }
        this.name = name;
        this.permission = permission;
    }

    @ConstructorProperties(value={"name", "aliases", "permission", "description", "async", "hidden", "method", "owningClass", "validFlags", "parameters", "children", "parent", "logToConsole"})
    public CommandNode(@NonNull String name, Set<String> aliases, @NonNull String permission, String description, boolean async, boolean hidden, Method method, Class<?> owningClass, List<String> validFlags, List<Data> parameters, Map<String, CommandNode> children, CommandNode parent, boolean logToConsole) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (permission == null) {
            throw new NullPointerException("permission");
        }
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.description = description;
        this.async = async;
        this.hidden = hidden;
        this.method = method;
        this.owningClass = owningClass;
        this.validFlags = validFlags;
        this.parameters = parameters;
        this.children = children;
        this.parent = parent;
        this.logToConsole = logToConsole;
    }

    public CommandNode() {
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    @NonNull
    public String getPermission() {
        return this.permission;
    }

    public void setPermission(@NonNull String permission) {
        if (permission == null) {
            throw new NullPointerException("permission");
        }
        this.permission = permission;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAsync() {
        return this.async;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getOwningClass() {
        return this.owningClass;
    }

    public List<String> getValidFlags() {
        return this.validFlags;
    }

    public void setValidFlags(List<String> validFlags) {
        this.validFlags = validFlags;
    }

    public List<Data> getParameters() {
        return this.parameters;
    }

    public void setParameters(List<Data> parameters) {
        this.parameters = parameters;
    }

    public Map<String, CommandNode> getChildren() {
        return this.children;
    }

    public CommandNode getParent() {
        return this.parent;
    }

    public void setParent(CommandNode parent) {
        this.parent = parent;
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }
}

