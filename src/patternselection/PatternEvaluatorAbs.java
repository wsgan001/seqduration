package patternselection;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import patterncreater.PatternUtil;
import test.Print;

public abstract class PatternEvaluatorAbs {

	protected final static double MIN_NUMBER = 0.001;

	protected final Map<Integer, List<String>> my_db;

	protected final int my_sizeOfClasses;
	// occurrence probability of each class in the database
	protected double[] my_ratioOfClasses;
	// conditional patterns' presence[0] and absence[1] in each class
	// the last one represents the occurrence of patterns.
	protected double[][] my_ratioOfPatterns;

	public PatternEvaluatorAbs(final String a_db_file, final int the_sizeOfClasses) throws FileNotFoundException {
		my_db = PatternUtil.getDB(a_db_file, the_sizeOfClasses);
		// System.out.println("db: "+my_db);
		my_sizeOfClasses = the_sizeOfClasses;
		calcClassRatio();
	}

	/**
	 * get the probability of each class
	 */
	protected void calcClassRatio() {
		my_ratioOfClasses = new double[my_sizeOfClasses + 1];
		for (int i : my_db.keySet()) {
			my_ratioOfClasses[i] = my_db.get(i).size();
			my_ratioOfClasses[my_sizeOfClasses] += my_ratioOfClasses[i];
		}
		// System.out.println("get class ratio without normalisation");
		// Print.printArray(my_ratioOfClasses);
	}

	protected double[] getClassRatio() {
		double[] result = new double[my_sizeOfClasses];
		for (int i = 0; i < my_sizeOfClasses; i++) {
			result[i] = my_ratioOfClasses[i] * 1.0 / my_ratioOfClasses[my_sizeOfClasses];
		}
		return result;
	}

	protected void calcPatternRatio(final String a_pattern) {
		my_ratioOfPatterns = new double[2][my_sizeOfClasses + 1];
		for (int i : my_db.keySet()) {
			for (String s : my_db.get(i))
				if (PatternUtil.contains(s, a_pattern)) {
					my_ratioOfPatterns[0][i] += 1;
					my_ratioOfPatterns[0][my_sizeOfClasses] += 1;
				} else {
					my_ratioOfPatterns[1][i] += 1;
					my_ratioOfPatterns[1][my_sizeOfClasses] += 1;
				}
		}
		// System.out.println("get pattern ratio: ");
		// Print.printArray(my_ratioOfPatterns);
	}

	/**
	 * get the probability of the given pattern double[0 - (sizeOfClasses - 1)]:
	 * conditional probability on each class double[sizeOfClass] last one:
	 * occurrence probability of the pattern [0]: pattern present [1]: pattern
	 * absent
	 * 
	 * @param a_pattern
	 */
	protected double[][] getConditionalRatioOnPatterns(final String a_pattern) {
		// System.out.println("\nget normalised conditional patterns:");
		double[][] result = new double[2][my_sizeOfClasses + 1];
		for (int i = 0; i < result[0].length - 1; i++) {
			result[0][i] = my_ratioOfPatterns[0][i] * 1.0 / my_ratioOfPatterns[0][my_sizeOfClasses];
			result[1][i] = my_ratioOfPatterns[1][i] * 1.0 / my_ratioOfPatterns[1][my_sizeOfClasses];
		}
		result[0][my_sizeOfClasses] = my_ratioOfPatterns[0][my_sizeOfClasses] / my_ratioOfClasses[my_sizeOfClasses];
		result[1][my_sizeOfClasses] = my_ratioOfPatterns[1][my_sizeOfClasses] / my_ratioOfClasses[my_sizeOfClasses];
		// Print.printArray(result);
		return result;
	}

	/**
	 * get the probability of the given pattern double[0 - (sizeOfClasses - 1)]:
	 * conditional probability on each class double[sizeOfClass] last one:
	 * occurrence probability of the pattern [0]: pattern present [1]: pattern
	 * absent
	 * 
	 * @param a_pattern
	 */
	protected double[][] getConditionalRatioOnClasses(final String a_pattern) {
		double[][] result = new double[2][my_sizeOfClasses + 1];
		for (int i = 0; i < result[0].length; i++) {
			if (my_ratioOfClasses[i] == 0) {
				result[0][i] = 0;
				result[1][i] = 0;
			} else {
				result[0][i] = my_ratioOfPatterns[0][i] * 1.0 / my_ratioOfClasses[i];
				result[1][i] = my_ratioOfPatterns[1][i] * 1.0 / my_ratioOfClasses[i];
			}
		}
//		System.out.println("normalise conditional prob: ");
//		Print.printArray(result);
		return result;
	}

}
