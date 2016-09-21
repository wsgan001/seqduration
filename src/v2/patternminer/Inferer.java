package v2.patternminer;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;
import patterncreater.PatternUtil;
import patterninference.EditDistanceCalc;
import sensor.SensorEvent;
import sensor.SensorUtil;
import test.Print;
import test.TestInferer;
import v2.data.DataUtil;
import v2.data.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * 1. read a list of sensor events 2.
 * 
 * @author juan
 *
 */
public class Inferer {
	private static double E = 0.001;
	private static DecimalFormat df = new DecimalFormat("#.###");
	private static double match_weight = 0.8;

	private FileWriter my_fileWriter;
	private int[] my_acts;
	private Map<Integer, Map<String, double[]>> my_act_pattern_score;

	public Inferer(FileWriter a_fw, final String a_pattern_score_file, final int[] acts)
			throws ClassNotFoundException, IOException {
		initialisePatternScore(a_pattern_score_file, acts);
		my_acts = acts;
		// printSetup();
		my_fileWriter = a_fw;
	}

	/**
	 * 
	 * @param a_pattern_score_file
	 *            E.g.,
	 *            raw_train/patternscores/act_trainFileIndex_actIndex.score,
	 *            given raw_train/patternscores/act_trainFileIndex_
	 * @param the_numOfClasses
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void initialisePatternScore(final String appendix_trainIndex, final int[] the_acts)
			throws IOException, ClassNotFoundException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, the_acts);
		my_act_pattern_score = new HashMap<Integer, Map<String, double[]>>();
		for (int i = 0; i < the_acts.length; i++) {
			FileInputStream fis = new FileInputStream(
					group_dir + Parameters.PATTERNS_SCORE_DIR + appendix_trainIndex + the_acts[i]);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_act_pattern_score.put(i, (Map<String, double[]>) ois.readObject());
			fis.close();
			ois.close();
		}
	}

	private void writeArray(double[] a, FileWriter a_fw) throws IOException {
		for (int i = 0; i < a.length; i++) {
			a_fw.write(a[i] + ",");
		}
	}

	/**
	 * Might need to update
	 * 
	 * @param match_score
	 * @param pattern_scores
	 * @return
	 * @throws IOException
	 */
	private double computeScore(final double match_score, final int pattern_length, final double[] pattern_scores) throws IOException {
		// my_fileWriter.write("match: " + match_score + "\t pattern score: ");
		// writeArray(pattern_scores, my_fileWriter);
		// double result = 0;
		// for (int i = 0; i < pattern_scores.length; i++) {
		// if (pattern_scores[i] != 0.0) {
		// result += pattern_scores[i];
		// }
		// }
		// result = result * (1 - match_weight) + Math.log(match_score) *
		// match_weight;
		// return result;
		// System.out.println("MD = " + df.format(match_score) + ", " +
		// df.format(pattern_scores[0]) + ", "
		// + df.format(pattern_scores[1]) + ", " +
		// df.format(pattern_scores[2]));
		// return match_weight * match_score
		// + (1 - match_weight) * (pattern_scores[0] / Math.pow(Math.E,
		// pattern_scores[1]));
		return (1 - match_score/pattern_length) + 0.1 * (pattern_scores[0] / Math.pow(Math.E, pattern_scores[1]));
	}

	private double[] initialiseDoubleArray(int the_length) {
		double[] result = new double[the_length];
		for (int i = 0; i < the_length; i++) {
			result[i] = Double.NEGATIVE_INFINITY;
		}
		return result;
	}

	public void startInferring(final String seq_file) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(seq_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<Integer> acts = (List<Integer>) ois.readObject();
		List<String> patterns = (List<String>) ois.readObject();
		fis.close();
		ois.close();
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> gt = new ArrayList<Integer>();
		for (int pi = 0; pi < patterns.size(); pi++) {
			my_fileWriter.write("GT:"+acts.get(pi) + "\t");
			double[] in = infer(patterns.get(pi));
			int i = TestInferer.getMax(in);
			if (i >= 0) {
				result.add(my_acts[i]);
				gt.add(acts.get(pi));
				my_fileWriter.write("==>" + i + ", (" + acts.get(pi) + ")\n\n");
			} else {
				System.out.println("NO MATHCED SEQ: "+patterns.get(pi));
			}
		}
		Evaluator eval = new Evaluator(gt, result, my_acts);
		my_fileWriter.write("accuracies: " + df.format(eval.getAccuracy()) + "\n");
		// my_fileWriter.write("cm: \n" + eval.printCM() + "\n");
		double[] cl = eval.getClassAccuracy();
		for (int i = 0; i < cl.length; i++) {
			my_fileWriter.write(df.format(cl[i]) + "\t");
		}
	}

	public double[] infer(final String seq) throws IOException {
		my_fileWriter.write("input: " + seq + "\n ");
		final int seq_length  = seq.split(" ").length;
		double[] max = initialiseDoubleArray(my_acts.length);
		for (int act : my_act_pattern_score.keySet()) {
			// my_fileWriter.write("\nA" + act + " ");
//			System.out.println("A"+act+" ");
			String bestP = "";
			for (String p : my_act_pattern_score.get(act).keySet()) {
				// my_fileWriter.write("P " + p + ": ");
//				System.out.println(p + ":");
				final double m = EditDistanceCalc.compute(seq, p);
				if (m < seq_length) {
					final double match_degree =
							// EditDistanceCalc.compute(seq, p);
							computeScore(m, seq_length, my_act_pattern_score.get(act).get(p));
					my_fileWriter.write("A" + act + "\tP " + p + "\n");
					my_fileWriter
							.write("d=" + df.format(m) + ", |sq|="+seq_length+", m="+df.format(1-m/seq_length)+ ", p=" + df.format(my_act_pattern_score.get(act).get(p)[0])
									+ ", " + df.format(my_act_pattern_score.get(act).get(p)[1]) + ", F="
									+ df.format(match_degree) + "\n");
//					System.out.println("m=" + df.format(m) + ", p="
//							+ df.format(my_act_pattern_score.get(act).get(p)[0]) + ", "
//							+ df.format(my_act_pattern_score.get(act).get(p)[1]) + ", F=" + df.format(match_degree));
					if (match_degree > max[act]) {
						max[act] = match_degree;
						bestP = p;
					}
				}
			}
			if (my_act_pattern_score.containsKey(act) && my_act_pattern_score.get(act).containsKey(bestP)) {
				my_fileWriter.write("best matched p: " + bestP + ": " + df.format(max[act]) + ", "
						+ df.format(my_act_pattern_score.get(act).get(bestP)[0]) + ", "
						+ df.format(my_act_pattern_score.get(act).get(bestP)[1]) + ", "
						+ df.format(my_act_pattern_score.get(act).get(bestP)[2]) + "\n");
			} else {
				my_fileWriter.write(act + " - " + bestP + " not in " + (my_act_pattern_score.containsKey(act)) + " "
						+ (my_act_pattern_score.get(act).containsKey(bestP)) + "\n");
			}
		}
//		System.out.println("result for each act: ");
//		Print.printArray(max);
		return max;
	}

	public static void run(final int[] acts, final String appendix) throws ClassNotFoundException, IOException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		{
			File f = new File(group_dir + Parameters.PATTERNS_RESULT_DIR);
			if (!f.exists())
				f.mkdir();
		}
		for (int i = 0; i < 10; i++) {
			FileWriter fw = new FileWriter(group_dir + Parameters.PATTERNS_RESULT_DIR + appendix + i);
			Inferer inf = new Inferer(fw, appendix + i + "_", acts);
			inf.startInferring(group_dir + Parameters.PATTERNS_SQUENTIAL_CONVERTED_TEST_DIR + appendix + i);
			fw.close();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		run(Parameters.R1_ROOM, Parameters.SOURCE);
	}

}
