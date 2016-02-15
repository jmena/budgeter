package me.bgx.budget.util;

import java.lang.reflect.Constructor;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.translate.CreateContext;
import com.googlecode.objectify.impl.translate.LoadContext;
import com.googlecode.objectify.impl.translate.SaveContext;
import com.googlecode.objectify.impl.translate.SkipException;
import com.googlecode.objectify.impl.translate.TypeKey;
import com.googlecode.objectify.impl.translate.ValueTranslator;
import com.googlecode.objectify.impl.translate.ValueTranslatorFactory;

import me.bgx.budget.model.data.Project;
import me.bgx.budget.model.data.RegisteredUser;
import me.bgx.budget.model.data.Simulation;
import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.generators.Generator;
import static com.googlecode.objectify.ObjectifyService.factory;

public class ObjectifyInitializer {

    static class StringConstructorTranslatorFactory<T> extends ValueTranslatorFactory<T, String> {
        Class<T> clazz;

        StringConstructorTranslatorFactory(Class<T> clazz) {
            super(clazz);
            this.clazz = clazz;
        }

        @Override
        protected ValueTranslator<T, String> createValueTranslator(TypeKey<T> tk, CreateContext ctx, Path path) {
            return new ValueTranslator<T, String>(String.class) {
                @Override
                protected T loadValue(String value, LoadContext ctx, Path path) throws SkipException {
                    try {
                        Constructor<T> ctor;
                        ctor = getConstructor(String.class);
                        if (ctor == null) {
                            ctor = getConstructor(Object.class);
                        }
                        return ctor.newInstance(value);
                    } catch (Exception e) {
                        throw new RuntimeException("Unable to get constructor for:" + clazz.getSimpleName(), e);
                    }
                }

                private Constructor<T> getConstructor(Class<?> ctorArgType) {
                    try {
                        return clazz.getConstructor(ctorArgType);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                }

                @Override
                protected String saveValue(T value, boolean index, SaveContext ctx, Path path) throws SkipException {
                    return value.toString();
                }
            };
        }
    }

    static {
        factory().getTranslators().add(new StringConstructorTranslatorFactory<>(Period.class));
        factory().getTranslators().add(new StringConstructorTranslatorFactory<>(LocalDate.class));

        factory().register(Project.class);
        factory().register(Simulation.class);
        factory().register(RegisteredUser.class);

        factory().register(Rule.class);
        for (Class<?> ruleClass : Rule.ALL_RULES) {
            factory().register(ruleClass);
        }
    }
}
