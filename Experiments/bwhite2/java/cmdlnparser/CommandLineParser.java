import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.function.Function;

public class CommandLineParser {

	private static Map<Class<?>, String> defaultValues = new HashMap<>();
	private static Map<Class<?>, Function<String, ?>> conversionFunctions = new HashMap<>();

	static {
		defaultValues.put(boolean.class, "false");
		defaultValues.put(byte.class, "0");
		defaultValues.put(char.class, "\u0000");
		defaultValues.put(double.class, "0.0");
		defaultValues.put(float.class, "0.0");
		defaultValues.put(int.class, "0");
		defaultValues.put(long.class, "0");
		defaultValues.put(short.class, "0");
		defaultValues.put(Boolean.class, "false");
		defaultValues.put(Byte.class, "0");
		defaultValues.put(Character.class, "\u0000");
		defaultValues.put(Double.class, "0.0");
		defaultValues.put(Float.class, "0.0");
		defaultValues.put(Integer.class, "0");
		defaultValues.put(Long.class, "0");
		defaultValues.put(Short.class, "0");
		conversionFunctions.put(boolean.class, x -> Boolean.valueOf(x).booleanValue());
		conversionFunctions.put(byte.class, x -> Byte.valueOf(x).byteValue());
		conversionFunctions.put(char.class, x -> x.charAt(0));
		conversionFunctions.put(double.class, x -> Double.valueOf(x).doubleValue());
		conversionFunctions.put(float.class, x -> Float.valueOf(x).floatValue());
		conversionFunctions.put(int.class, x -> Integer.valueOf(x).intValue());
		conversionFunctions.put(long.class, x -> Long.valueOf(x).longValue());
		conversionFunctions.put(short.class, x -> Short.valueOf(x).shortValue());
		conversionFunctions.put(Boolean.class, Boolean::valueOf);
		conversionFunctions.put(Byte.class, Byte::valueOf);
		conversionFunctions.put(Character.class, x -> Character.valueOf(x.charAt(0)));
		conversionFunctions.put(Double.class, Double::valueOf);
		conversionFunctions.put(Float.class, Float::valueOf);
		conversionFunctions.put(Integer.class, Integer::valueOf);
		conversionFunctions.put(Long.class, Long::valueOf);
		conversionFunctions.put(Short.class, Short::valueOf);
		conversionFunctions.put(String.class, x -> x);
	}

	public static void configureDefaults(Map<Class<?>, String> configuredDefaultValues) {
		defaultValues.putAll(configuredDefaultValues);
	}

	public static void configureConversions(Map<Class<?>, Function<String, ?>> configuredConversionFunctions) {
		conversionFunctions.putAll(configuredConversionFunctions);
	}

	public static <T> T execute(Class<T> prgm, String[] args) throws Exception {
		return execute(prgm.getConstructor().newInstance(), args);
	}

	public static <T> T execute(T instance, String[] args) throws Exception {
		Class<?> prgm = instance.getClass();
		boolean storenext = false;
		String flag = null;
		Map<String, String> flagValues = new HashMap<>();
		List<String> values = new ArrayList<>();
		for(String i : args) {
			if(storenext && i.startsWith("-")) flag = i.substring(1);
			else if(i.startsWith("-")) {
				flag = i.substring(1);
				flagValues.put(flag, "true");
				storenext = true;
			} else if(storenext) {
				flagValues.put(flag, i);
				storenext = false;
			} else values.add(i);
		}
		for(Field i : prgm.getDeclaredFields()) {
			if(i.isAnnotationPresent(Flag.class) || i.isAnnotationPresent(Value.class)) {
				Class<?> type = i.getType();
				String value = null;
				if(i.isAnnotationPresent(Flag.class)) value = flagValues.get(i.getDeclaredAnnotation(Flag.class).value());
				else if(i.getDeclaredAnnotation(Value.class).value() < values.size()) value = values.get(i.getDeclaredAnnotation(Value.class).value());
				if(type == String.class && value == null) continue;
				Function<String, ?> transformer = conversionFunctions.get(type);
				String defaultValue = defaultValues.get(type);
				i.setAccessible(true);
				if(transformer != null) {
					try {
						i.set(instance, transformer.apply(value));
					} catch(Exception e) {
						if(!i.isAnnotationPresent(NoDefault.class)) i.set(instance, transformer.apply(defaultValue));
					}
				} else if(defaultValue != null) i.set(instance, defaultValue);
			}
		}
		return instance;
	}
}