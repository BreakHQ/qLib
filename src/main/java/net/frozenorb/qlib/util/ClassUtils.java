/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.util;

import com.google.common.collect.ImmutableSet;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.plugin.Plugin;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

public final class ClassUtils {
    private ClassUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        HashSet classes = new HashSet();
        for (URL url : ClasspathHelper.forClassLoader(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader(), plugin.getClass().getClassLoader())) {
            try (Vfs.Dir dir = Vfs.fromURL(url);){
                for (Vfs.File file : dir.getFiles()) {
                    String name = file.getRelativePath().replace("/", ".").replace(".class", "");
                    if (!name.startsWith(packageName)) continue;
                    classes.add(Class.forName(name));
                }
            }
        }
        return ImmutableSet.copyOf(classes);
    }
}

