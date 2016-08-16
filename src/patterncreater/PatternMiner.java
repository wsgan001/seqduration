package patterncreater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import parameters.FileAddresses;
import parameters.Symbols;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoBIDEPlus;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVGEN;

public class PatternMiner {
	final private static String SEP = " ";

	private int my_topK = 10;

	private double my_support_ratio;

	private String my_database;
	/**
	 * the top k patterns for each length of patterns <pattern, double[0]:
	 * support count, double[1]: information gain>
	 */
	private Map<String, double[]> my_patterns;

	public PatternMiner(final String the_db_file, final double support_ratio) {
		my_database = the_db_file;
		my_support_ratio = support_ratio;
		my_patterns = new HashMap<String, double[]>();
	}

	private String getSeq(String the_line) {
		String result = "";
		String[] split = the_line.split(SEP);
		for (int i = 0; i < split.length; i++) {
			if (split[i].contains("#SUP")) {
				break;
			} else if (!split[i].contains("-1") && !split[i].isEmpty()) {
				result += split[i] + SEP;
			}
		}
		return result;
	}

	private int retrieveCount(String the_line) {
		String[] split = the_line.split(SEP);
		return Integer.parseInt(split[split.length - 1]);
	}

	public void selectPattern() throws IOException {
		Scanner sc = new Scanner(new File(my_database
				+ FileAddresses.PATTERN_AFFIX));
		int count = 1;
		int last_size = 0;

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String seq = getSeq(line);
			if (seq.trim().split(SEP).length > last_size) {
				last_size = seq.trim().split(SEP).length;
				count = 1;
			} else if (count < my_topK) {
				my_patterns.put(seq, new double[] { retrieveCount(line), 0 });
				count++;
			}
		}
		{
			FileOutputStream fos = new FileOutputStream(my_database
					+ FileAddresses.PATTERN_TOPK);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(my_patterns);
			oos.close();
			fos.close();
		}
	}

	public void mine() throws IOException {
		boolean keepPatterns = true;
		boolean verbose = false;

		AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative
				.getInstance();
		boolean dfs = true;

		// if you set the following parameter to true, the sequence ids of the
		// sequences where
		// each pattern appears will be shown in the result
		boolean outputSequenceIdentifiers = false;

		IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();

		CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative
				.getInstance();

		SequenceDatabase sequenceDatabase = new SequenceDatabase(
				abstractionCreator, idListCreator);

		sequenceDatabase.loadFile(my_database, my_support_ratio);

		// my_noOfLines = sequenceDatabase.size();
		// System.out.println("db: "+sequenceDatabase.toString());

		// AlgoCMSPADE algorithm = new AlgoCMSPADE(my_support_ratio, dfs,
		// abstractionCreator);

		// AlgoBIDEPlus_withStrings algorithm = new AlgoBIDEPlus_withStrings();
	

		// algorithm.runAlgorithm(sequenceDatabase, candidateGenerator,
		// keepPatterns, verbose, my_database
		// + FileAddresses.PATTERN_AFFIX,
		// outputSequenceIdentifiers);

//		final int minsup = (int) (getLinesOfFile(my_database) * my_support_ratio);
//		AlgoBIDEPlus algorithm = new AlgoBIDEPlus();
//		algorithm.runAlgorithm(my_database, my_database
//				+ FileAddresses.PATTERN_AFFIX,minsup);
		AlgoVGEN algo = new AlgoVGEN(); 
		algo.runAlgorithm(my_database, my_database
				+ FileAddresses.PATTERN_AFFIX, my_support_ratio);  
		
//		System.out.println("Relative Minimum support = " + my_support_ratio);
//		System.out.println(algorithm.getNumberOfFrequentPatterns()
//				+ " frequent patterns.");

		// System.out.println(algorithm.printStatistics());
	}

	private int getLinesOfFile(final String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		int lines = 0;
		while (reader.readLine() != null)
			lines++;
		reader.close();
		return lines;
	}

}
