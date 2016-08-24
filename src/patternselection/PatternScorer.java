package patternselection;

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

public class PatternScorer {

	private static int MAX_SCORE_INDEX = 5;
	private Map<String, double[]> my_patterns;
//	private PatternEvaluator my_evaluator;
	private String my_db_file;
	private int numOfActs;
	// private int my_score_index;

	public PatternScorer(final String a_db_file, final int the_sizeOfClasses, final String a_mipattern_addr,
			final String file_prefix) throws FileNotFoundException {
		// initialiseEvaluator(an_eval, a_db_file, the_sizeOfClasses);
		// getScoreIndex(an_eval);
		my_db_file = a_db_file;
		numOfActs = the_sizeOfClasses;
		my_patterns = new HashMap<String, double[]>();
		System.out.println("db file: " + a_db_file + "\npattern: " + a_mipattern_addr + "\nfile: " + file_prefix);
		getPatterns(a_mipattern_addr, file_prefix);
//		printPatterns();
	}

	public PatternScorer(final String a_pattern_score_file, final String a_db_file, final int the_sizeOfClasses)
			throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(a_pattern_score_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		my_patterns = (Map<String, double[]>) ois.readObject();
		my_db_file = a_db_file;
		numOfActs = the_sizeOfClasses;
	}
	
	private void printPatterns() {
		for(String s: my_patterns.keySet()) {
			System.out.println(s+": "+my_patterns.get(s)[0]);
		}
	}

	public Map<String, double[]> addScore(final Evaluators an_eval) throws IOException {
		PatternEvaluator pe = getEvaluator(an_eval, my_db_file, numOfActs);
		if (pe != null) {
			return start(pe, getScoreIndex(an_eval));
		} else {
			return null;
		}
	}

	private int getScoreIndex(final Evaluators an_eval) {
		if (an_eval == Evaluators.InformationGain) {
			return 1;
		} else if (an_eval == Evaluators.MutualInformation) {
			return 2;
		} else {
			return -1;
		}
	}

	public Map<String, double[]> getPatternScores() {
		return my_patterns;
	}

	private Map<String, double[]> start(final PatternEvaluator a_pe, final int a_scoreIndex) throws IOException {
		// Map<String, double[]> result = new HashMap<String, double[]>();
		for (String p : my_patterns.keySet()) {
			if (p != null && !p.isEmpty()) {
				double score = a_pe.getScore(p);
				my_patterns.get(p)[a_scoreIndex] = score;
//				System.out.println(p+": "+score);
			}
		}
		// serialise(my_patterns, the_output_file);
		return my_patterns;
	}

	public void serialise(final Map<String, double[]> pattern_scores, final String the_output_file) throws IOException {
		FileOutputStream fos = new FileOutputStream(the_output_file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(pattern_scores);
		oos.close();
		fos.close();
	}

	private PatternEvaluator getEvaluator(final Evaluators an_eval, final String a_db_file, final int the_sizeOfClasses)
			throws FileNotFoundException {
		if (an_eval == Evaluators.InformationGain) {
			return new InformationGainEvaluator(a_db_file, the_sizeOfClasses);
		} else if (an_eval == Evaluators.MutualInformation) {
			return new MutualInformationEvaluator(a_db_file, the_sizeOfClasses);
		} else {
			return null;
		}
	}

	public void evaluatePatterns(final String a_mipattern_addr, final String file_prefix) throws FileNotFoundException {
		my_patterns = new HashMap<String, double[]>();
		getPatterns(a_mipattern_addr, file_prefix);
	}

	public void getPatterns(final String a_mipattern_addr, final String file_prefix) throws FileNotFoundException {
		File folder = new File(a_mipattern_addr);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().startsWith(file_prefix)
					&& listOfFiles[i].getName().endsWith(FileAddresses.PATTERN_AFFIX)) {
//				System.out.println("File " + listOfFiles[i].getName());
				getPatterns(a_mipattern_addr + listOfFiles[i].getName());
			}
		}
	}

	public void getPatterns(final String a_file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(a_file));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (!line.isEmpty()) {
				String[] split = line.split(Symbols.PATTERN_SEPARATOR_IT);
				my_patterns.put(split[0], new double[] { Double.parseDouble(split[1]), 0, 0, 0, 0 });
			}
		}
		sc.close();
	}

}
