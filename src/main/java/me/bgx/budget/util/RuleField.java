package me.bgx.budget.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RuleField {
    String label() default "";
    String type() default "";
    int order() default 0;
    String additionalInformation() default "";
}
