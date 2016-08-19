package patternselection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import parameters.FileAddresses;
import parameters.Symbols;

public class PatternScorer {

	private Map<String, Double> my_patterns;
	private PatternEvaluator my_evaluator;

	public PatternScorer(final Evaluators an_eval, final String a_db_file, final int the_sizeOfClasses,
			final String a_mipattern_addr, final String file_prefix) throws FileNotFoundException {
		initialiseEvaluator(an_eval, a_db_file, the_sizeOfClasses);
		getPatterns(a_mipattern_addr, file_prefix);
	}

	public Map<String, Double> start() {
		Map<String, Double> result = new HashMap<String, Double>();
		for (String p : my_patterns.keySet()) {
			double score = my_evaluator.getScore(p);
			result.put(p, score + Math.log(my_patterns.get(p)));
		}
		return result;
	}

	private void serialise(final Map<String, Double> pattern_scores, final String the_output_file) {

	}

	private void initialiseEvaluator(final Evaluators an_eval, final String a_db_file, final int the_sizeOfClasses)
			throws FileNotFoundException {
		if (an_eval == Evaluators.InformationGain) {
			my_evaluator = new InformationGainEvaluator(a_db_file, the_sizeOfClasses);
		} else if (an_eval == Evaluators.MutualInformation) {
			my_evaluator = new MutualInformationEvaluator(a_db_file, the_sizeOfClasses);
		}
	}

	public void evaluatePatterns(final String a_mipattern_addr, final String file_prefix) throws FileNotFoundException {
		my_patterns = new HashMap<String, Double>();
		getPatterns(a_mipattern_addr, file_prefix);
	}

	private void getPatterns(final String a_mipattern_addr, final String file_prefix) throws FileNotFoundException {
		File folder = new File(a_mipattern_addr);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().startsWith(file_prefix)
					&& listOfFiles[i].getName().endsWith(FileAddresses.PATTERN_AFFIX)) {
				System.out.println("File " + listOfFiles[i].getName());
				getPatterns(a_mipattern_addr + listOfFiles[i].getName());
			}
		}
	}

	private void getPatterns(final String a_file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(a_file));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (!line.isEmpty()) {
				String[] split = line.split(Symbols.PATTERN_SEPARATOR_IT);
				my_patterns.put(split[0], Double.parseDouble(split[1]));
			}
		}
		sc.close();
	}

}
