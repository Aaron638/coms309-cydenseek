package annotationtests;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.PARAMETER;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ParameterAnnotation {}