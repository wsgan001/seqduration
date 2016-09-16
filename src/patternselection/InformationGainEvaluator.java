package patternselection;

import java.io.FileNotFoundException;
import java.util.List;

import test.Print;

public class InformationGainEvaluator extends PatternEvaluatorAbs implements PatternEvaluator {

	public InformationGainEvaluator(String a_db_file, int the_sizeOfClasses) throws FileNotFoundException {
		super(a_db_file, the_sizeOfClasses);
	}

	@Override
	public double getScore(String a_pattern) {
		double[] classRatio = getClassRatio();
		calcPatternRatio(a_pattern);
		double[][] patternRatio = getConditionalRatioOnPatterns(a_pattern);
		// print:
//		System.out.println("for pattern: " + a_pattern);
		// System.out.println("class ratio: ");
		// Print.printArray(classRatio);
//		System.out.println("\nconditional pattern ratio:");
//		Print.printArray(patternRatio);
		double first = 0, second = 0, third = 0;
		for (int i = 0; i < my_sizeOfClasses; i++) {
			// if (classRatio[i] == 0) {
			// first += min();
			// } else {
			// first += classRatio[i] * Math.log(classRatio[i]);
			// }
			if (patternRatio[0][i] != 0) {
				second += -1 * patternRatio[0][i] * log(patternRatio[0][i]);
			}
//			if (patternRatio[0][i] == 0) {
//				second += min();
//			} else {
//				second += -1 * patternRatio[0][i] * log(patternRatio[0][i]);
//			}
			// if (patternRatio[1][i] == 0) {
			// third += min();
			// } else {
			// third += patternRatio[1][i] * Math.log(patternRatio[1][i]);
			// }
		}
		// second *= patternRatio[0][my_sizeOfClasses];
		// third *= patternRatio[1][my_sizeOfClasses];
		// return -1 * first + second + third;
//		System.out.println("information gain: " + second);
		return second;
	}

	private double log(double v) {
		return Math.log(v) / Math.log(my_sizeOfClasses);
	}

	private double min() {
		return PatternEvaluatorAbs.MIN_NUMBER * Math.log(PatternEvaluatorAbs.MIN_NUMBER);
	}

}
