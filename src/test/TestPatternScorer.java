package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import parameters.FileAddresses;
import patternselection.Evaluators;
import patternselection.InformationGainEvaluator;
import patternselection.PatternEvaluator;
import patternselection.PatternScorer;

public class TestPatternScorer {

	final static Evaluators[] eval = new Evaluators[]{Evaluators.InformationGain, Evaluators.MutualInformation};
	final static String db_file = FileAddresses.RAW_TRAIN_DB;
	final static int NUMOFACTs = 25;
	final static String mipattern_addr = "data/raw_train/mipatterns/";
	// to find the right pattern. add train data index
	final static String file_prefix = "act_";

	// method to evaluate patterns
	public static void run() throws IOException {
		for (int ti = 0; ti < 10; ti++) {
			for (int ai = 0; ai < NUMOFACTs; ai++) {
				PatternScorer ps = new PatternScorer(db_file + ti + "_", NUMOFACTs, mipattern_addr,
						file_prefix + ti + "_" + ai + ".");
				ps.addScore(eval[0]);
				ps.addScore(eval[1]);
				ps.serialise(ps.getPatternScores(), FileAddresses.RAW_TRAIN_PATTERN_SCORE + ti + "_" + ai + ".score");
			}
		}
	}

	// method to retrieve and test
	public static void testPatternLoader() throws FileNotFoundException {
		final int ti = 0;
		final int ai = 1;
		PatternScorer ps = new PatternScorer(db_file, NUMOFACTs, mipattern_addr, file_prefix + ti + "_" + ai + ".");
		// System.out.println("file: "+file_prefix);
		// ps.getPatterns(mipattern_addr, file_prefix + ti + "_" + ai);
		Map<String, double[]> patterns = ps.getPatternScores();
		for (String p : patterns.keySet()) {
			System.out.println(p + ": " + patterns.get(p)[0]);
		}
	}

	public static void testPatternEvaluator() throws FileNotFoundException {
		final int ti = 0;
		final int ai = 1;
		PatternEvaluator pe = new InformationGainEvaluator(db_file+ti+"_",NUMOFACTs);
		
	}

	public static void main(String[] args) throws IOException {
		// testPatternLoader();
		run();
//		testPatternEvaluator();
	}
}
