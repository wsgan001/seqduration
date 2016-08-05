package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import parameters.FileAddresses;
import pattern.PatternMiner;
import pattern.SequentialMiner;

public class TestPatternMiner {

	final private static int ACT_SIZE = 25;
	final private static double support_ratio = 0.5;
	final private static int topK = 20;

	public static void mine() throws IOException {
		for (int i = 0; i < ACT_SIZE; i++) {
			System.out.println(i);
			// PatternMiner pm = new PatternMiner(FileAddresses.PATTERN + i,
			// support_ratio);
			// pm.mine();
			SequentialMiner sm = new SequentialMiner(FileAddresses.RAW_DB + i, FileAddresses.RAW_MINED_PATTERN + i,
					support_ratio, topK);
			sm.buildInternalModel();
			// {
			// FileOutputStream fos = new
			// FileOutputStream(FileAddresses.PATTERN+i+FileAddresses.PATTERN_AFFIX);
			// ObjectOutputStream oos = new ObjectOutputStream(fos);
			// oos.writeObject(sm.candidates);
			// oos.close();
			// fos.close();
			// }
		}
	}

	public static void selectP() throws IOException {
		for (int i = 0; i < ACT_SIZE; i++) {
			System.out.println(i);
			PatternMiner pm = new PatternMiner(FileAddresses.RAW_DB + i, support_ratio);
			pm.selectPattern();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("mining");
		mine();
		// System.out.println("selecting");
		// selectP();
	}

}
