package patternselection;

import java.io.FileNotFoundException;

public class MutualInformationEvaluator extends PatternEvaluatorAbs implements PatternEvaluator {

	public MutualInformationEvaluator(String a_db_file, int the_sizeOfClasses) throws FileNotFoundException {
		super(a_db_file, the_sizeOfClasses);
	}

	@Override
	public double getScore(String a_pattern) {
		calcPatternRatio(a_pattern);
		double[][] condOnClasses = getConditionalRatioOnClasses(a_pattern);
		// double[] mi = new double[my_sizeOfClasses];
		double mi = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < my_sizeOfClasses; i++) {
			if (condOnClasses[0][i] > 0) {
				double m = Math.log(condOnClasses[0][i] / condOnClasses[0][my_sizeOfClasses]);
				if (m > mi) {
					mi = m;
				}
			}
		}
		// getMI(my_ratioOfPatterns);
		// System.out.println(a_pattern + ": " + mi);
		return mi;
	}

	private double[] getMI(double[][] the_patternRatio) {
		double[] result = new double[my_sizeOfClasses];
		for (int i = 0; i < my_sizeOfClasses; i++) {
			// both class and pattern occur
			double A = the_patternRatio[0][i];
			// class occurs without pattern
			double C = the_patternRatio[1][i];
			// pattern occurs without class
			double B = the_patternRatio[0][my_sizeOfClasses] - the_patternRatio[0][i];
			// the total number of instances
			double N = my_ratioOfClasses[my_sizeOfClasses];
			if (A == 0 && B == 0 && C == 0) {
				result[i] = Math.log(PatternEvaluatorAbs.MIN_NUMBER);
			} else {
				result[i] = Math.log(A * N / ((A + C) * (A + B)));
			}
		}
		return result;
	}

}
