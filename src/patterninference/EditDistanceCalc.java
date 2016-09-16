package patterninference;

import parameters.Symbols;

public class EditDistanceCalc {
	
	private static int intersect(String s, String[] p) {
		int count = 0;
		for(String sp: p) {
			if (s.contains(sp+" ")) {
				count++;
			}
		}
		return count;
	}

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
//		 System.out.println(a+", "+b+" : "+(1 - distance[sa.length][sb.length] / Math.max(sa.length, sb.length)));
		// Print.printDoubleArray(distance);
		return (1 - distance[sa.length][sb.length] / sa.length);
//				* intersect(a, sb);
	}

	private static double minimum(double a, double b, double c) {
		return Math.min(Math.min(a, b), c);
	}

	public static void main(String[] args) {
		String p = "221 278 278 278 284 268 264 247 225 221 237 260 247 225 221 221 221 221 237 260 ";
		String a = "221 221 221 221 221 221 ";
		String b = "247 260 247 264 260 247 264 260 247 260 ";
		String[] s = {"221", "278"};
		System.out.println(intersect(b, s));
		System.out.println(compute(p, a)+", "+compute(p,b));
	}

}
