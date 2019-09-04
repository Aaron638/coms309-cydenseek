package annotationtests;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;

@Retention(RUNTIME)
@Target(METHOD)
public @interface MethodAnnotation {}