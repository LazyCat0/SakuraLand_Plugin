package dev.lazycat.sakuraLand.someFeatures;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AiCoded {
    String by() default "unknown";
    String id() default "00000";
    String ai() default "unknown";
}
