package v2.patternminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import parameters.FileAddresses;
import parameters.Symbols;
import patternselection.Evaluators;
import v2.data.DataUtil;
import v2.data.Parameters;

public class PatternScorer {

	private static int MAX_SCORE_INDEX = 5;
	// private Map<String, double[]> my_patterns;
	// private PatternEvaluator my_evaluator;
	private String my_db_file;
	private int[] my_acts;
	// private int my_score_index;

	public static void run(final int[] the_acts, final String appendix) throws IOException {
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			new PatternScorer(the_acts, appendix, i);
		}
	}

	public PatternScorer(final int[] the_acts, final String appendix, final int train_index) throws IOException {
		// initialiseEvaluator(an_eval, a_db_file, the_sizeOfClasses);
		// getScoreIndex(an_eval);
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, the_acts);
		my_db_file = group_dir + Parameters.PATTERNS_SQUENTIAL_CONVERTED_FILES_DIR + appendix + train_index + "_";
		my_acts = the_acts;
		for (int aId : the_acts) {
			PatternEvaluator pe = new PatternEvaluator(
					group_dir + Parameters.PATTERNS_SQUENTIAL_CONVERTED_FILES_DIR + appendix + train_index + "_",
					the_acts);
			start(pe, group_dir, appendix + train_index + "_" + aId);
		}
	}

	public void start(final PatternEvaluator pe, final String group_dir, final String file_prefix) throws IOException {
		Map<String, double[]> my_patterns = getPatterns(group_dir + Parameters.PATTERNS_SQUENTIAL_PATTERNS_DIR,
				file_prefix);
		for (String p : my_patterns.keySet()) {
			pe.getScores(p, my_patterns.get(p));
		}
		// serialise the scores
		FileOutputStream fos = new FileOutputStream(group_dir + Parameters.PATTERNS_SCORE_DIR + file_prefix);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(my_patterns);
		oos.flush();
		oos.close();
		fos.close();
	}

	public Map<String, double[]> getPatterns(final String a_mipattern_addr, final String file_prefix)
			throws FileNotFoundException {
		File folder = new File(a_mipattern_addr);
		File[] listOfFiles = folder.listFiles();
		Map<String, double[]> my_patterns = new HashMap<String, double[]>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().startsWith(file_prefix)
					&& listOfFiles[i].getName().endsWith(FileAddresses.PATTERN_AFFIX)) {
				// System.out.println("File " + listOfFiles[i].getName());
				getPatterns(a_mipattern_addr + listOfFiles[i].getName(), my_patterns);
			}
		}
		return my_patterns;
	}

	public void getPatterns(final String a_file, Map<String, double[]> patterns) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(a_file));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (!line.isEmpty()) {
				String[] split = line.split(Symbols.PATTERN_SEPARATOR_IT);
				patterns.put(split[0], new double[] { Double.parseDouble(split[1]), 0, 0, 0, 0 });
			}
		}
		sc.close();
	}

	public static void main(String[] args) throws IOException {
		run(Parameters.R1_ROOM, Parameters.SPLIT);
	}

}
