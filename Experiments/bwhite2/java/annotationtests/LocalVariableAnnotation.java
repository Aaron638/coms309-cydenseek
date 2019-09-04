package annotationtests;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;

@Retention(RUNTIME)
@Target(LOCAL_VARIABLE)
public @interface LocalVariableAnnotation {}