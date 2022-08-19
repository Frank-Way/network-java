package serialization.annotations;

import java.lang.annotation.*;

@Inherited
@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface YamlSerializable {
    boolean delegateValidationToEquals() default false;
}