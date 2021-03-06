package me.bgx.budget.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.bgx.budget.model.generators.Generator;
import me.bgx.budget.model.generators.GeneratorBase;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IsRule {
    String type();
    String label() default "";
    Class<? extends GeneratorBase> generator();
}
