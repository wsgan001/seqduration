package v2.patternminer;

import java.io.FileNotFoundException;

public class MutualInformationEvaluator{

	public static double getScore(double[][] conditionalRatio) {
		// double[] mi = new double[my_sizeOfClasses];
		double mi = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < conditionalRatio[0].length-1; i++) {
			if (conditionalRatio[0][i] > 0) {
				double m = Math.log(conditionalRatio[0][i] / conditionalRatio[0][conditionalRatio[0].length-1]);
				if (m > mi) {
					mi = m;
				}
			}
		}
		// getMI(my_ratioOfPatterns);
		// System.out.println(a_pattern + ": " + mi);
		return mi;
	}

	private static double[] getMI(double[][] the_patternRatio, double[] classRatio) {
		double[] result = new double[the_patternRatio[0].length-1];
		for (int i = 0; i < the_patternRatio[0].length-1; i++) {
			// both class and pattern occur
			double A = the_patternRatio[0][i];
			// class occurs without pattern
			double C = the_patternRatio[1][i];
			// pattern occurs without class
			double B = the_patternRatio[0][the_patternRatio[0].length-1] - the_patternRatio[0][i];
			// the total number of instances
			double N = classRatio[the_patternRatio[0].length-1];
			if (A == 0 && B == 0 && C == 0) {
				result[i] = Math.log(PatternEvaluator.MIN_NUMBER);
			} else {
				result[i] = Math.log(A * N / ((A + C) * (A + B)));
			}
		}
		return result;
	}

}
