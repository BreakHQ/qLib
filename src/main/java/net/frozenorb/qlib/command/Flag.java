/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Flag {
    public static final Pattern FLAG_PATTERN = Pattern.compile("(-)([a-zA-Z])([\\w]*)?");

    public String[] value();

    public boolean defaultValue() default false;

    public String description() default "";
}

