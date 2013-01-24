package de.upbracing.code_generation.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// This annotation can only be used on methods.
@Target(ElementType.METHOD)
// We can access the annotation information at runtime.
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationMethod {
	String name() default "";
}
