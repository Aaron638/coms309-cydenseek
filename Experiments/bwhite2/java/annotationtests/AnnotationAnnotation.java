package annotationtests;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface AnnotationAnnotation {}