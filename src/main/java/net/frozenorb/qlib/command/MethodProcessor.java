/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.qlib.command;

import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandNode;
import net.frozenorb.qlib.command.Data;
import net.frozenorb.qlib.command.Flag;
import net.frozenorb.qlib.command.FlagData;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.command.ParameterData;
import net.frozenorb.qlib.command.Processor;
import net.frozenorb.qlib.command.Type;
import org.bukkit.command.CommandSender;

public class MethodProcessor
implements Processor<Method, Set<CommandNode>> {
    @Override
    public Set<CommandNode> process(Method value) {
        if (value.isAnnotationPresent(Command.class) && value.getParameterCount() >= 1 && CommandSender.class.isAssignableFrom(value.getParameterTypes()[0])) {
            Command command = value.getAnnotation(Command.class);
            Class<?> owningClass = value.getDeclaringClass();
            ArrayList<String> flagNames = new ArrayList<String>();
            ArrayList<Data> allParams = new ArrayList<Data>();
            if (value.getParameterCount() > 1) {
                for (int i = 1; i < value.getParameterCount(); ++i) {
                    Data data;
                    Parameter parameter = value.getParameters()[i];
                    if (parameter.isAnnotationPresent(Param.class)) {
                        Param param = parameter.getAnnotation(Param.class);
                        data = new ParameterData(param.name(), param.defaultValue(), parameter.getType(), param.wildcard(), i, Sets.newHashSet((Object[])param.tabCompleteFlags()), parameter.isAnnotationPresent(Type.class) ? parameter.getAnnotation(Type.class).value() : null);
                        allParams.add(data);
                        continue;
                    }
                    if (parameter.isAnnotationPresent(Flag.class)) {
                        Flag flag = parameter.getAnnotation(Flag.class);
                        data = new FlagData(Arrays.asList(flag.value()), flag.description(), flag.defaultValue(), i);
                        allParams.add(data);
                        flagNames.addAll(Arrays.asList(flag.value()));
                        continue;
                    }
                    throw new IllegalArgumentException("Every parameter, other than the sender, must have the Param or the Flag annotation! (" + value.getDeclaringClass().getName() + ":" + value.getName() + ")");
                }
            }
            HashSet<CommandNode> registered = new HashSet<CommandNode>();
            for (String name : command.names()) {
                boolean first = true;
                boolean change = true;
                boolean hadChild = false;
                String[] cmdNames = (name = name.toLowerCase().trim()).contains(" ") ? name.split(" ") : new String[]{name};
                String primary = cmdNames[0];
                CommandNode workingNode = new CommandNode(owningClass);
                if (FrozenCommandHandler.ROOT_NODE.hasCommand(primary)) {
                    workingNode = FrozenCommandHandler.ROOT_NODE.getCommand(primary);
                    change = false;
                }
                if (change) {
                    workingNode.setName(cmdNames[0]);
                } else {
                    workingNode.getAliases().add(cmdNames[0]);
                }
                CommandNode parentNode = new CommandNode(owningClass);
                if (workingNode.hasCommand(cmdNames[0])) {
                    parentNode = workingNode.getCommand(cmdNames[0]);
                } else {
                    parentNode.setName(cmdNames[0]);
                    parentNode.setPermission("");
                }
                if (cmdNames.length > 1) {
                    hadChild = true;
                    workingNode.registerCommand(parentNode);
                    CommandNode childNode = new CommandNode(owningClass);
                    for (int i = 1; i < cmdNames.length; ++i) {
                        String subName = cmdNames[i];
                        childNode.setName(subName);
                        if (parentNode.hasCommand(subName)) {
                            childNode = parentNode.getCommand(subName);
                        }
                        parentNode.registerCommand(childNode);
                        if (i == cmdNames.length - 1) {
                            childNode.setMethod(value);
                            childNode.setAsync(command.async());
                            childNode.setHidden(command.hidden());
                            childNode.setPermission(command.permission());
                            childNode.setDescription(command.description());
                            childNode.setValidFlags(flagNames);
                            childNode.setParameters(allParams);
                            childNode.setLogToConsole(command.logToConsole());
                            continue;
                        }
                        parentNode = childNode;
                        childNode = new CommandNode(owningClass);
                    }
                }
                if (!hadChild) {
                    parentNode.setMethod(value);
                    parentNode.setAsync(command.async());
                    parentNode.setHidden(command.hidden());
                    parentNode.setPermission(command.permission());
                    parentNode.setDescription(command.description());
                    parentNode.setValidFlags(flagNames);
                    parentNode.setParameters(allParams);
                    parentNode.setLogToConsole(command.logToConsole());
                    workingNode.registerCommand(parentNode);
                }
                first = false;
                FrozenCommandHandler.ROOT_NODE.registerCommand(workingNode);
                registered.add(workingNode);
            }
            return registered;
        }
        return null;
    }
}

