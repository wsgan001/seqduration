package v2.patternminer;

import java.io.FileNotFoundException;
import java.util.List;

import test.Print;

public class InformationGainEvaluator {

	public static double getScore(double[] classRatio, double[][] patternRatio) {
		// double[][] patternRatio = getConditionalRatioOnPatterns(a_pattern);
		// print:
		// System.out.println("for pattern: " + a_pattern);
		// System.out.println("class ratio: ");
		// Print.printArray(classRatio);
		// System.out.println("\nconditional pattern ratio:");
		// Print.printArray(patternRatio);
		double first = 0, second = 0, third = 0;
		for (int i = 0; i < classRatio.length - 1; i++) {
			// if (classRatio[i] == 0) {
			// first += min();
			// } else {
			// first += classRatio[i] * Math.log(classRatio[i]);
			// }
			if (patternRatio[0][i] != 0) {
				second += -1 * patternRatio[0][i] * log(patternRatio[0][i], classRatio.length - 1);
			}
			// if (patternRatio[0][i] == 0) {
			// second += min();
			// } else {
			// second += -1 * patternRatio[0][i] * log(patternRatio[0][i]);
			// }
			// if (patternRatio[1][i] == 0) {
			// third += min();
			// } else {
			// third += patternRatio[1][i] * Math.log(patternRatio[1][i]);
			// }
		}
		// second *= patternRatio[0][my_sizeOfClasses];
		// third *= patternRatio[1][my_sizeOfClasses];
		// return -1 * first + second + third;
		// System.out.println("information gain: " + second);
		return second;
	}

	private static double log(double v, int size) {
		return Math.log(v) / Math.log(size);
	}

	private static double min() {
		return PatternEvaluator.MIN_NUMBER * Math.log(PatternEvaluator.MIN_NUMBER);
	}

}
