import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Value {
	int value();
}