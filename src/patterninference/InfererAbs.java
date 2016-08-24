package patterninference;

import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;
import patterncreater.PatternUtil;
import sensor.SensorEvent;
import sensor.SensorUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

/**
 * 1. read a list of sensor events 2.
 * 
 * @author juan
 *
 */
public class InfererAbs {
	private static double E = 0.001;
	/**
	 * map<act id, map<pattern string, scores>>
	 */
	private Map<Integer, Map<String, double[]>> my_act_pattern_score;
	private List<String> my_2Lpattern_indices;
	private Map<String, List<DescriptiveStatistics>> my_2Lpattern_clusters;

	private FileWriter my_fileWriter;

	private static double match_weight = 0.5;
	private int my_numOfAct;
	private SensorUtil my_sensors;

	public InfererAbs(SensorUtil the_sensors, FileWriter a_fw, final String a_pattern_score_file,
			final int the_numOfClasses, final String a_2LPattern_file, final String a_2LP_index_file)
			throws ClassNotFoundException, IOException {
		initialisePatternScore(a_pattern_score_file, the_numOfClasses);
		initialise2LPattern(a_2LPattern_file, a_2LP_index_file);
		printSetup();
		my_fileWriter = a_fw;
		my_sensors = the_sensors;
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
	private void initialisePatternScore(final String a_pattern_score_file, final int the_numOfClasses)
			throws IOException, ClassNotFoundException {
		my_numOfAct = the_numOfClasses;
		my_act_pattern_score = new HashMap<Integer, Map<String, double[]>>();
		for (int i = 0; i < the_numOfClasses; i++) {
			FileInputStream fis = new FileInputStream(
					a_pattern_score_file + i + FileAddresses.RAW_TRAIN_PATTERN_SCORE_AFFIX);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_act_pattern_score.put(i, (Map<String, double[]>) ois.readObject());
			fis.close();
			ois.close();
		}
	}

	private void initialise2LPattern(final String a_2LPattern_file, final String a_2LP_index_file)
			throws IOException, ClassNotFoundException {
		{
			FileInputStream fis = new FileInputStream(a_2LPattern_file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_2Lpattern_clusters = (Map<String, List<DescriptiveStatistics>>) ois.readObject();
			fis.close();
			ois.close();
		}
		{
			FileInputStream fis = new FileInputStream(a_2LP_index_file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_2Lpattern_indices = (List<String>) ois.readObject();
			fis.close();
			ois.close();
		}
	}

	private void printSetup() {
		System.out.println("2L Sequence");
		for (String k : my_2Lpattern_clusters.keySet()) {
			System.out.print("\n" + k + ": ");
			for (DescriptiveStatistics ds : my_2Lpattern_clusters.get(k)) {
				System.out.print(ds.getMean() + ", ");
			}
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
	private double computeScore(final double match_score, final double[] pattern_scores) throws IOException {
		// my_fileWriter.write("match: " + match_score + "\t pattern score: ");
		// writeArray(pattern_scores, my_fileWriter);
		double result = 0;
		for (int i = 0; i < pattern_scores.length; i++) {
			if (pattern_scores[i] != 0.0) {
				result += pattern_scores[i];
			}
		}
		result = result * (1 - match_weight) + Math.log(match_score) * match_weight;
		return result;
	}

	private double[] initialiseDoubleArray(int the_length) {
		double[] result = new double[the_length];
		for (int i = 0; i < the_length; i++) {
			result[i] = Double.NEGATIVE_INFINITY;
		}
		return result;
	}

	public double[] infer(final List<SensorEvent> a_listOfEvents) throws IOException {
		String seq = map22LPattern(a_listOfEvents);
		// my_fileWriter.write("pattern: "+seq+"\n start searching:");
		double[] max = initialiseDoubleArray(my_numOfAct);
		for (int act : my_act_pattern_score.keySet()) {
			// my_fileWriter.write("\nA" + act + " ");
			for (String p : my_act_pattern_score.get(act).keySet()) {
				// my_fileWriter.write("P " + p + ": ");
				final double match_degree = computeScore(EditDistanceCalc.compute(seq, p),
						my_act_pattern_score.get(act).get(p));
				if (match_degree > max[act]) {
					max[act] = match_degree;
				}
			}
		}
		return max;
	}

	private String getMatchedPattern(final int from, final int to, final long duration) {
		String result = "";
		double max = 0;
		for (String p : my_2Lpattern_clusters.keySet()) {
			double s = get2LSeqMatchScore(p, from + Symbols.SEQ_SEPARATOR + to);
			double[] d = getDurationScore(duration, my_2Lpattern_clusters.get(p));
			if (d[0] + s > max) {
				max = d[0] + s;
				result = p + Symbols.DURATION_SEPARATOR + new Double(d[1]).intValue();
			}
		}
		// System.out.println("given " + from + "-" + to + " end: " + result + "
		// : " + max);
		return result;
	}

	private double[] getDurationScore(final long duration, final List<DescriptiveStatistics> ds) {
		double[] max = new double[2];
		for (int i = 0; i < ds.size(); i++) {
			double density = new NormalDistribution(ds.get(i).getMean(), ds.get(i).getStandardDeviation())
					.density(duration);
			if (density > max[0]) {
				max[0] = density;
				max[1] = i;
			}
		}
		return max;
	}

	private String map22LPattern(final List<SensorEvent> a_listOfEvents) {
		String result = "";
		for (int i = 0; i < a_listOfEvents.size() - 1; i++) {
			// final String the_2lPattern = a_listOfEvents.get(i).getSensorId()
			// + Symbols.SEQ_SEPARATOR
			// + a_listOfEvents.get(i + 1).getSensorId();
			long duration = a_listOfEvents.get(i + 1).getStartTime() - a_listOfEvents.get(i).getStartTime();
			result += get2LPatternIndex(getMatchedPattern(a_listOfEvents.get(i).getSensorId(),
					a_listOfEvents.get(i + 1).getSensorId(), duration)) + Symbols.PATTERN_SEPARATOR;
			// result += get2LPatternIndex(getClusterIndex(the_2lPattern,
			// duration)) + Symbols.PATTERN_SEPARATOR;
		}
		return result;
	}

	private String getClusterIndex(final String a_2Lpattern, final long duration) {
		int id = -1;
		for (String p : my_2Lpattern_clusters.keySet()) {
			if (p.equals(a_2Lpattern)) {
				int i = PatternUtil.map2cluster(my_2Lpattern_clusters.get(p), duration);
				id = i;
				break;
			}
		}
		if (id >= 0) {
			return a_2Lpattern + Symbols.DURATION_SEPARATOR + id;
		} else {
			String p = findBestMatched2LSeqP(a_2Lpattern);
			int i = PatternUtil.map2cluster(my_2Lpattern_clusters.get(p), duration);
			return p + Symbols.DURATION_SEPARATOR + i;
		}

	}

	private String findBestMatched2LSeqP(final String a_2Lpattern) {
		double score = 0;
		String result = "";
		for (String p : my_2Lpattern_clusters.keySet()) {
			double s = get2LSeqMatchScore(p, a_2Lpattern);
			if (s > score) {
				score = s;
				result = p;
			}
		}
		// System.out.println(
		// " no pattern found for: " + a_2Lpattern + ", but closed one is: " +
		// result + " with score: " + score);
		return result;
	}

	private int get2LPatternIndex(final String a_2LPattern) {
		for (int i = 0; i < my_2Lpattern_indices.size(); i++) {
			if (my_2Lpattern_indices.get(i).equals(a_2LPattern)) {
				// System.out.println(a_2LPattern + " map to " + i);
				return i;
				// } else {
				// double s = get2LSeqMatchScore(my_2Lpattern_indices.get(i),
				// a_2LPattern);
				// if (s > score) {
				// score = s;
				// id = i;
				// }
			}
		}
		// System.out.println("no match for: " + a_2LPattern);

		return -1;
	}

	private double get2LSeqMatchScore(final String a_2LSeq, final String input) {
		// System.out.println("pattern: "+a_2LSeq+", input: "+input);
		String[] seq = a_2LSeq.trim().split(Symbols.SEQ_SEPARATOR);
		String[] in = input.trim().split(Symbols.SEQ_SEPARATOR);
		if (seq[0].equals(in[1]) && seq[1].equals(in[0])) {
			return 1;
		} else {
			if (seq[0].equals(in[0])) {
				return 0.6 + (my_sensors.similarity(my_sensors.findSensor(Integer.parseInt(seq[1])),
						my_sensors.findSensor(Integer.parseInt(in[1]))) - E) * 0.4;
			} else if (seq[1].equals(in[1])) {
				return 0.4 + (my_sensors.similarity(my_sensors.findSensor(Integer.parseInt(seq[0])),
						my_sensors.findSensor(Integer.parseInt(in[0]))) - E) * 0.6;
			} else {
				return (my_sensors.similarity(my_sensors.findSensor(Integer.parseInt(seq[0])),
						my_sensors.findSensor(Integer.parseInt(in[0]))) - E) * 0.6
						+ (my_sensors.similarity(my_sensors.findSensor(Integer.parseInt(seq[1])),
								my_sensors.findSensor(Integer.parseInt(in[1]))) - E) * 0.4;
			}
		}
		// result += 1;

		// for (int i = 0; i < seq.length; i++) {
		// if (seq[i].equals(in[i])) {
		// result += 1;
		// } else {
		// result +=
		// my_sensors.similarity(my_sensors.findSensor(Integer.parseInt(seq[i])),
		// my_sensors.findSensor(Integer.parseInt(in[i]))) - E;
		// }
		// }
	}

}
