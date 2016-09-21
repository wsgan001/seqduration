package v2.patternminer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {

	private int[][] confusionMatrix;
	private int total;
	private Map<Integer, Integer> actId_index;

	public Evaluator(List<Integer> ground_truth, List<Integer> inferred, int[] acts) {
		getActIdIndexMap(acts);
		initialise(ground_truth, inferred, acts.length);
	}

	private void getActIdIndexMap(int[] acts) {
		actId_index = new HashMap<Integer, Integer>();
		int index = 0;
		for (int a : acts) {
			actId_index.put(a, index++);
		}
	}

	public double[] getClassAccuracy() {
		double[] result = new double[confusionMatrix.length];
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix.length; j++) {
				result[i] += confusionMatrix[i][j];
			}
		}
		for (int i = 0; i < confusionMatrix.length; i++) {
			if (result[i] > 0) {
				result[i] = confusionMatrix[i][i] / result[i];
			} else {
				result[i] = 0;
			}
		}
		return result;
	}

	private void initialise(List<Integer> ground_truth, List<Integer> inferred, int numOfClasses) {
		confusionMatrix = new int[numOfClasses][numOfClasses];
		total = ground_truth.size();
		for (int i = 0; i < ground_truth.size(); i++) {
			try {
				if (inferred.get(i) >= 0) {
//					System.out.println(i+": "+ground_truth.get(i)+", "+inferred.get(i)+"->"+actId_index.get(ground_truth.get(i)));
					confusionMatrix[actId_index.get(ground_truth.get(i))][actId_index.get(inferred.get(i))] += 1;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(
						confusionMatrix.length + ", " + i + ": " + ground_truth.get(i) + ", " + inferred.get(i));

			}
		}
	}

	public String printCM() {
		String result = "";
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix[i].length; j++) {
				result += confusionMatrix[i][j] + "\t";
			}
			result += "\n";
		}
		return result;
	}

	public double getAccuracy() {
		int c = 0;
		for (int i = 0; i < confusionMatrix.length; i++) {
			c += confusionMatrix[i][i];
		}
		return c * 1.0 / total;
	}

}
