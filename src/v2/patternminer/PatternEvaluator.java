package v2.patternminer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.Print;

public class PatternEvaluator {

	protected final static int NUM_OF_SCORES = 5;
	protected final static double MIN_NUMBER = 0.001;

	protected final Map<Integer, List<String>> my_db;

	protected double[] my_ratioOfClasses;
	// conditional patterns' presence[0] and absence[1] in each class
	// the last one represents the occurrence of patterns.
//	protected double[][] my_ratioOfPatterns;

	protected int[] my_acts;

	public PatternEvaluator(final String a_db_file, final int[] acts) throws FileNotFoundException {
		my_acts = acts;
		System.out.println("db_file: "+a_db_file);
		my_db = PatternUtil.getDB(a_db_file, acts);
		// System.out.println("db: "+my_db);
		calcClassRatio();
	}
	
	public void getScores(String a_pattern, double[] scores) {
		final double[][] ratioOfPatterns =  calcPatternRatio(a_pattern);
		final double[] classRatio = getClassRatio();
		double[][] conditionalProb = getConditionalRatioOnPatterns(ratioOfPatterns);
		// occurrence ratio in all the data
		scores[1] = conditionalProb[0][classRatio.length];
		// Information gain
		scores[2] = InformationGainEvaluator.getScore(classRatio, conditionalProb);
		
	}
	

	// private void initialiseIndex() {
	// my_actID_index = new HashMap<Integer, Integer>();
	// int index = 0;
	// for (int i : my_acts) {
	// my_actID_index.put(i, index);
	// index++;
	// }
	// }

	/**
	 * get the probability of each class
	 */
	protected void calcClassRatio() {
		my_ratioOfClasses = new double[my_acts.length + 1];
		for (int i =0; i< my_acts.length; i++) {
			my_ratioOfClasses[i] = my_db.get(my_acts[i]).size();
			my_ratioOfClasses[my_acts.length] += my_ratioOfClasses[i];
		}
		// System.out.println("get class ratio without normalisation");
		// Print.printArray(my_ratioOfClasses);
	}

	protected double[] getClassRatio() {
		double[] result = new double[my_acts.length];
		for (int i = 0; i < my_acts.length; i++) {
			result[i] = my_ratioOfClasses[i] * 1.0 / my_ratioOfClasses[my_acts.length];
		}
		return result;
	}

	protected double[][] calcPatternRatio(final String a_pattern) {
		double[][] my_ratioOfPatterns = new double[2][my_acts.length + 1];
		for (int i = 0; i < my_acts.length; i++) {
			for (String s : my_db.get(my_acts[i]))
				if (PatternUtil.contains(s, a_pattern)) {
					my_ratioOfPatterns[0][i] += 1;
					my_ratioOfPatterns[0][my_acts.length] += 1;
				} else {
					my_ratioOfPatterns[1][i] += 1;
					my_ratioOfPatterns[1][my_acts.length] += 1;
				}
		}
		return my_ratioOfPatterns;
		// System.out.println("get pattern ratio: ");
		// Print.printArray(my_ratioOfPatterns);
	}

	/**
	 * prob(c|p) get the probability of the given pattern double[0 -
	 * (sizeOfClasses - 1)]: conditional probability on each class
	 * double[sizeOfClass] last one: occurrence probability of the pattern [0]:
	 * pattern present [1]: pattern absent
	 * 
	 * @param a_pattern
	 */
	protected double[][] getConditionalRatioOnPatterns(double[][] my_ratioOfPatterns) {
		// System.out.println("\nget normalised conditional patterns:");
		double[][] result = new double[2][my_acts.length + 1];
		for (int i = 0; i < result[0].length - 1; i++) {
			result[0][i] = my_ratioOfPatterns[0][i] * 1.0 / my_ratioOfPatterns[0][my_acts.length];
			result[1][i] = my_ratioOfPatterns[1][i] * 1.0 / my_ratioOfPatterns[1][my_acts.length];
		}
		result[0][my_acts.length] = my_ratioOfPatterns[0][my_acts.length] / my_ratioOfClasses[my_acts.length];
		result[1][my_acts.length] = my_ratioOfPatterns[1][my_acts.length] / my_ratioOfClasses[my_acts.length];
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
	protected double[][] getConditionalRatioOnClasses(double[][] my_ratioOfPatterns) {
		double[][] result = new double[2][my_acts.length + 1];
		for (int i = 0; i < result[0].length; i++) {
			if (my_ratioOfClasses[i] == 0) {
				result[0][i] = 0;
				result[1][i] = 0;
			} else {
				result[0][i] = my_ratioOfPatterns[0][i] * 1.0 / my_ratioOfClasses[i];
				result[1][i] = my_ratioOfPatterns[1][i] * 1.0 / my_ratioOfClasses[i];
			}
		}
		// System.out.println("normalise conditional prob: ");
		// Print.printArray(result);
		return result;
	}

}
