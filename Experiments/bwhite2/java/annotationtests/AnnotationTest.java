package annotationtests;

@ClassAnnotation
public class AnnotationTest {

	@FieldAnnotation
	private static String field;

	@ConstructorAnnotation
	private AnnotationTest() {}

	@MethodAnnotation
	public static void main(@ParameterAnnotation String[] args) throws Exception {
		@LocalVariableAnnotation
		String local;
		System.out.println("AnnotationTest class has ClassAnnotation annotation: " +
			AnnotationTest.class
			.isAnnotationPresent(ClassAnnotation.class)
		);
		System.out.println("AnnotationTest class has field with FieldAnnotation annotation: " +
			AnnotationTest.class
			.getDeclaredField("field")
			.isAnnotationPresent(FieldAnnotation.class)
		);
		System.out.println("AnnotationTest class has constructor with ConstructorAnnotation annotation: " +
			AnnotationTest.class
			.getDeclaredConstructor()
			.isAnnotationPresent(ConstructorAnnotation.class)
		);
		System.out.println("AnnotationTest main method has MethodAnnotation annotation: " +
			AnnotationTest.class
			.getDeclaredMethod("main", String[].class)
			.isAnnotationPresent(MethodAnnotation.class)
		);
		System.out.println("AnnotationTest main method has parameter with ParameterAnnotation annotation: " +
			AnnotationTest.class
			.getDeclaredMethod("main", String[].class)
			.getParameters()[0]
			.isAnnotationPresent(ParameterAnnotation.class)
		);
	}

	@AnnotationAnnotation
	private @interface MyAnnotation {}
}