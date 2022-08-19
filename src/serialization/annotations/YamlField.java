package serialization.annotations;

import java.lang.annotation.*;

@Inherited
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface YamlField {
    String fieldName() default "";
}