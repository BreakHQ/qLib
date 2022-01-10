/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Param {
    public String name();

    public String defaultValue() default "";

    public String[] tabCompleteFlags() default {};

    public boolean wildcard() default false;
}

