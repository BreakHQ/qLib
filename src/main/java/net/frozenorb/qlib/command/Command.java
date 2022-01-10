/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Command {
    public String[] names();

    public String permission();

    public boolean hidden() default false;

    public boolean async() default false;

    public String description() default "";

    public boolean logToConsole() default true;
}

