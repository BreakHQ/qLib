/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.entity.Player
 *  org.bukkit.help.HelpTopic
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.qlib.command.CommandConfiguration;
import net.frozenorb.qlib.command.CommandNode;
import net.frozenorb.qlib.command.MethodProcessor;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.command.bukkit.FrozenCommand;
import net.frozenorb.qlib.command.bukkit.FrozenCommandMap;
import net.frozenorb.qlib.command.bukkit.FrozenHelpTopic;
import net.frozenorb.qlib.command.defaults.BuildCommand;
import net.frozenorb.qlib.command.defaults.CommandInfoCommand;
import net.frozenorb.qlib.command.defaults.EvalCommand;
import net.frozenorb.qlib.command.defaults.VisibilityDebugCommand;
import net.frozenorb.qlib.command.parameter.BooleanParameterType;
import net.frozenorb.qlib.command.parameter.DoubleParameterType;
import net.frozenorb.qlib.command.parameter.FloatParameterType;
import net.frozenorb.qlib.command.parameter.IntegerParameterType;
import net.frozenorb.qlib.command.parameter.ItemStackParameterType;
import net.frozenorb.qlib.command.parameter.OfflinePlayerParameterType;
import net.frozenorb.qlib.command.parameter.PlayerParameterType;
import net.frozenorb.qlib.command.parameter.StringParameterType;
import net.frozenorb.qlib.command.parameter.UUIDParameterType;
import net.frozenorb.qlib.command.parameter.WorldParameterType;
import net.frozenorb.qlib.command.parameter.filter.NormalFilter;
import net.frozenorb.qlib.command.parameter.filter.StrictFilter;
import net.frozenorb.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import net.frozenorb.qlib.command.parameter.offlineplayer.OfflinePlayerWrapperParameterType;
import net.frozenorb.qlib.command.utils.EasyClass;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class FrozenCommandHandler {
    public static CommandNode ROOT_NODE = new CommandNode();
    protected static Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP = new HashMap();
    protected static CommandMap commandMap;
    protected static Map<String, Command> knownCommands;
    private static CommandConfiguration config;

    public static void init() {
        FrozenCommandHandler.registerClass(BuildCommand.class);
        FrozenCommandHandler.registerClass(EvalCommand.class);
        FrozenCommandHandler.registerClass(CommandInfoCommand.class);
        FrozenCommandHandler.registerClass(VisibilityDebugCommand.class);
        new BukkitRunnable(){

            public void run() {
                try {
                    FrozenCommandHandler.swapCommandMap();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater((Plugin)qLib.getInstance(), 5L);
    }

    public static void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public static ParameterType getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

    public static CommandConfiguration getConfig() {
        return config;
    }

    public static void setConfig(CommandConfiguration config) {
        FrozenCommandHandler.config = config;
    }

    public static void registerMethod(Method method) {
        method.setAccessible(true);
        Set<CommandNode> nodes = new MethodProcessor().process(method);
        if (nodes != null) {
            nodes.forEach(node -> {
                if (node != null) {
                    FrozenCommand command = new FrozenCommand((CommandNode)node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));
                    FrozenCommandHandler.register(command);
                    node.getChildren().values().forEach(n -> FrozenCommandHandler.registerHelpTopic(n, node.getAliases()));
                }
            });
        }
    }

    protected static void registerHelpTopic(CommandNode node, Set<String> aliases) {
        if (node.method != null) {
            Bukkit.getHelpMap().addTopic((HelpTopic)new FrozenHelpTopic(node, aliases));
        }
        if (node.hasCommands()) {
            node.getChildren().values().forEach(n -> FrozenCommandHandler.registerHelpTopic(n, null));
        }
    }

    private static void register(FrozenCommand command) {
        try {
            Map<String, Command> knownCommands = FrozenCommandHandler.getKnownCommands();
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (!entry.getValue().getName().equalsIgnoreCase(command.getName())) continue;
                entry.getValue().unregister(commandMap);
                iterator.remove();
            }
            for (String alias : command.getAliases()) {
                knownCommands.put(alias, command);
            }
            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void registerClass(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            FrozenCommandHandler.registerMethod(method);
        }
    }

    public static void unregisterClass(Class<?> clazz) {
        Map<String, Command> knownCommands = FrozenCommandHandler.getKnownCommands();
        Iterator<Command> iterator = knownCommands.values().iterator();
        while (iterator.hasNext()) {
            CommandNode node;
            Command command = iterator.next();
            if (!(command instanceof FrozenCommand) || (node = ((FrozenCommand)command).getNode()).getOwningClass() != clazz) continue;
            command.unregister(commandMap);
            iterator.remove();
        }
    }

    public static void registerPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(FrozenCommandHandler::registerClass);
    }

    public static void registerAll(Plugin plugin) {
        FrozenCommandHandler.registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private static void swapCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        Object oldCommandMap = commandMapField.get((Object)Bukkit.getServer());
        FrozenCommandMap newCommandMap = new FrozenCommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & 0xFFFFFFEF);
        knownCommandsField.set((Object)newCommandMap, knownCommandsField.get(oldCommandMap));
        commandMapField.set((Object)Bukkit.getServer(), (Object)newCommandMap);
    }

    protected static CommandMap getCommandMap() {
        return (CommandMap)new EasyClass<Server>(Bukkit.getServer()).getField("commandMap").get();
    }

    protected static Map<String, Command> getKnownCommands() {
        return (Map)new EasyClass<CommandMap>(commandMap).getField("knownCommands").get();
    }

    static {
        config = new CommandConfiguration().setNoPermissionMessage("&cNo permission.");
        FrozenCommandHandler.registerParameterType(Boolean.TYPE, new BooleanParameterType());
        FrozenCommandHandler.registerParameterType(Integer.TYPE, new IntegerParameterType());
        FrozenCommandHandler.registerParameterType(Double.TYPE, new DoubleParameterType());
        FrozenCommandHandler.registerParameterType(Float.TYPE, new FloatParameterType());
        FrozenCommandHandler.registerParameterType(String.class, new StringParameterType());
        FrozenCommandHandler.registerParameterType(Player.class, new PlayerParameterType());
        FrozenCommandHandler.registerParameterType(World.class, new WorldParameterType());
        FrozenCommandHandler.registerParameterType(ItemStack.class, new ItemStackParameterType());
        FrozenCommandHandler.registerParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        FrozenCommandHandler.registerParameterType(UUID.class, new UUIDParameterType());
        FrozenCommandHandler.registerParameterType(OfflinePlayerWrapper.class, new OfflinePlayerWrapperParameterType());
        FrozenCommandHandler.registerParameterType(NormalFilter.class, new NormalFilter());
        FrozenCommandHandler.registerParameterType(StrictFilter.class, new StrictFilter());
        commandMap = FrozenCommandHandler.getCommandMap();
        knownCommands = FrozenCommandHandler.getKnownCommands();
    }
}

