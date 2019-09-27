import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class OutcomesGenerator {

	@Flag("o")
	private boolean order;

	@Flag("r")
	private boolean replacement;

	@Flag("k")
	@NoDefault
	private int k = 1;

	@Value(0)
	private String cmdValues = "";

	public static void main(String[] args) throws Exception {
		CommandLineParser.execute(OutcomesGenerator.class, args).run();
	}

	public void run() {
		String[] values = cmdValues.split(",");
		Arrays.sort(values);
		System.out.println("Ordered: " + order + "\nWith Replacement: " + replacement + "\nk = " + k + "\nn = " + values.length + "\nValues: " + Arrays.toString(values));
		if(order) {
			System.out.println("\nPermutations:");
			if(replacement) System.out.println(format(ordered_with_replacement(values)));
			else System.out.println(format(ordered_without_replacement(values)));
		} else {
			System.out.println("\nCombinations:");
			if(replacement) System.out.println(format(unordered_with_replacement(values)));
			else System.out.println(format(unordered_without_replacement(values)));
		}
	}

	private static String format(String[][] result) {
		return Arrays.deepToString(result).replaceAll("\\[\\[","[").replaceAll("\\]\\]","]").replaceAll(", \\[","\n[");
	}

	private String[][] unordered_without_replacement(String[] values) {
		List<String[]> sets = new ArrayList<>();
		int[] o = IntStream.range(0, k).toArray();
		while(true) {
			sets.add(Arrays.stream(o).mapToObj(x -> values[x]).toArray(String[]::new));
			if(o[0] == values.length - k) break;
			for(int i = k - 1; i >= 0; i--) {
				if(o[i] < values.length - k + i) {
					o[i]++;
					for(int j = i + 1; j < k; j++) o[j] = o[j - 1] + 1;
					break;
				}
			}
		}
		return sets.toArray(new String[][] {});
	}

	private String[][] ordered_with_replacement(String[] values) {
		List<String[]> sets = new ArrayList<>();
		int[] o = IntStream.range(0, k).map(x -> 0).toArray();
		while(true) {
			sets.add(Arrays.stream(o).mapToObj(x -> values[x]).toArray(String[]::new));
			if(Arrays.stream(o).filter(x -> x == values.length - 1).count() == k) break;
			for(int i = k - 1; i >= 0; i--) {
				if(o[i] < values.length - 1) {
					o[i]++;
					for(int j = i + 1; j < k; j++) o[j] = 0;
					break;
				}
			}
		}
		return sets.toArray(new String[][] {});
	}

	private String[][] ordered_without_replacement(String[] values) {
		List<String[]> sets = new ArrayList<>();
		int[] o = IntStream.range(0, k).toArray();
		while(true) {
			sets.add(Arrays.stream(o).mapToObj(x -> values[x]).toArray(String[]::new));
			if(IntStream.range(0, k).allMatch(x -> o[x] == values.length - x - 1)) break;
			for(int i = k - 1; i >= 0; i--) {
				if(o[i] < values.length - 1) {
					do o[i]++;
					while(o[i] < values.length && Arrays.stream(o).boxed().collect(Collectors.toList()).indexOf(o[i]) != i);
					if(o[i] == values.length) continue;
					for(int j = i + 1; j < k; j++) {
						o[j] = j - (i + 1);
						while(o[j] < values.length && Arrays.stream(o).boxed().collect(Collectors.toList()).indexOf(o[j]) != j) o[j]++;
						if(o[j] == values.length) continue;
					}
					break;
				}
			}
		}
		return sets.toArray(new String[][] {});
	}

	private String[][] unordered_with_replacement(String[] values) {
		List<String[]> sets = new ArrayList<>();
		int[] o = IntStream.range(0, k).map(x -> 0).toArray();
		while(true) {
			sets.add(Arrays.stream(o).mapToObj(x -> values[x]).toArray(String[]::new));
			if(Arrays.stream(o).filter(x -> x == values.length - 1).count() == k) break;
			for(int i = k - 1; i >= 0; i--) {
				if(o[i] < values.length - 1) {
					o[i]++;
					for(int j = i + 1; j < k; j++) o[j] = o[i];
					break;
				}
			}
		}
		return sets.toArray(new String[][] {});
	}
}