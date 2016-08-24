package patterninference;

import parameters.Symbols;

public class EditDistanceCalc {

	public static double compute(final String a, final String b) {
		String[] sa = a.trim().split(Symbols.PATTERN_SEPARATOR);
		String[] sb = b.trim().split(Symbols.PATTERN_SEPARATOR);
		double[][] distance = new double[sa.length + 1][sb.length + 1];
		for (int i = 0; i <= sa.length; i++) {
			distance[i][0] = i;
		}
		for (int j = 0; j <= sb.length; j++) {
			distance[0][j] = j;
		}
		for (int i = 1; i <= sa.length; i++) {
			for (int j = 1; j <= sb.length; j++) {
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1,
						distance[i - 1][j - 1] + ((sa[i - 1].equals(sb[j - 1])) ? 0 : 1));
			}
		}
		// System.out.println("final seq match:
		// "+distance[a_sequence.size()][b_sequence.size()]);
		// Print.printDoubleArray(distance);
		return 1 - distance[sa.length][sb.length] / Math.max(sa.length, sb.length);
	}

	private static double minimum(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}

	public static void main(String[] args) {
		String a = "234 345 123 678 674";
		String b = "234 345 123 678 674";
		System.out.println(compute(a, b));
	}

}
