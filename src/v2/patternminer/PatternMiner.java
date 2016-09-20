package v2.patternminer;

import java.io.IOException;

import patterncreater.SequentialMiner;
import patternselection.PatternScorer;
import v2.data.DataUtil;
import v2.data.Parameters;

public class PatternMiner {

	public static void run(final int[] acts, final String appendix) throws IOException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		final double support_ratio = 0.1;
		final int topK = 10;
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			for (int a = 0; a < acts.length; a++) {
				final String db = group_dir + Parameters.PATTERNS_SQUENTIAL_CONVERTED_FILES_DIR + appendix + i + "_"
						+ acts[a];
				final String output = group_dir + Parameters.PATTERNS_SQUENTIAL_PATTERNS_DIR + appendix + i + "_"
						+ acts[a];
				new SequentialMiner(db, output, support_ratio, topK);
				Runtime.getRuntime().freeMemory();
			}
		}
	}

//	public static void score(final int[] acts, final String appendix) {
//		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
//		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
//			for (int a = 0; a < acts.length; a++) {
//				PatternScorer ps = new PatternScorer(a_db_file, the_sizeOfClasses, a_mipattern_addr, file_prefix)
//			}
//		}
//
//	}

	public static void main(String[] args) throws IOException {
		run(Parameters.R1_ROOM, Parameters.SPLIT);
	}

}
