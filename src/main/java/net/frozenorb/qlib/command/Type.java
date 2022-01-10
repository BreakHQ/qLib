/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.frozenorb.qlib.command.ParameterType;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Type {
    public Class<? extends ParameterType> value();
}

