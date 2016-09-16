package patterncreater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import parameters.FileAddresses;

public class SequentialMiner2 {

	final protected static String CANDIDATE = ".tmp";

	protected static String SEPERATOR = " ";

	private double support_count;
	/**
	 * the number of previous situations
	 */
	// private int my_previous;

	/**
	 * database: transaction - itemset each outer-list is a transaction each
	 * inner-list is a sequence of items Integer: each item's id
	 */
	private List<String> database;

	private String my_db_file;
	
	private String my_candidate_file;

	public Map<String, Double> current_candidates;

	public Set<String> all_candidates;

	public int my_top_k;

	/**
	 * Map<a string consisting of ids of the previous-k situations, Map<id for
	 * the next situation, probability>>
	 */
	// private Map<String, Map<String, Double>> model;

	// /**
	// * record how many times the previous k locations occur.
	// */
	// private Map<String, Integer> occurrences;

	public SequentialMiner2(final String the_db_file, final String the_cand_file, final double the_support_count, final int the_top_k)
			throws FileNotFoundException {
		my_db_file = the_db_file;
		my_candidate_file = the_cand_file;
		getDB(the_db_file);
		support_count = the_support_count * database.size();
		current_candidates = new HashMap<String, Double>();
		all_candidates = new HashSet<String>();
		my_top_k = the_top_k;
	}

	private void getDB(final String the_db_file) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(the_db_file));
		database = new ArrayList<String>();
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String s = "";
			String[] split = line.split(SEPERATOR);
			for (int i = 0; i < split.length; i++) {
				if (!split[i].contains("-") && !split[i].isEmpty()) {
					s += split[i] + SEPERATOR;
				}
			}
			if (!s.isEmpty())
				database.add(s);
		}
	}

	private void writeCandidates(int k) throws IOException {
		FileWriter fw = new FileWriter(my_candidate_file + "." + k + FileAddresses.PATTERN_AFFIX);
		MapUtil.orderAndWrite(current_candidates, fw, my_top_k);
		fw.close();
		all_candidates.addAll(current_candidates.keySet());
		current_candidates = new HashMap<String, Double>();
	}

	private void oneItemCandidate() throws IOException {
		Map<String, Double> result = new HashMap<String, Double>();
		for (String transaction : database) {
			String[] items = transaction.split(SEPERATOR);
			for (String item : items) {
				double count = 0;
				if (result.containsKey(item + "")) {
					count = result.get(item + "");
				}
				count = count + 1;
				result.put(item, count);
			}
		}
		// Map<String, Double> cands = new HashMap<String, Double>();
		for (String cand : result.keySet()) {
			if (cand != null && !cand.isEmpty() && result.get(cand) >= support_count) {
				current_candidates.put(cand, result.get(cand));
				// System.out.println(cand+" "+result.get(cand));
			}
		}
		// candidates.add(0, cands);
		writeCandidates(0);
	}

	public void buildInternalModel() throws IOException {
		oneItemCandidate();
		int k = 1;
		boolean stop = false;
		do {
			System.out.println("pattern round k: " + k);
			// generate the kth round pattern candidates
			Map<String, Double> first_count = generate(k);
			count(first_count);
			for (String p : first_count.keySet()) {
				if (first_count.get(p) >= support_count) {
					current_candidates.put(p, first_count.get(p));
				}
			}
			if (!current_candidates.isEmpty()) {
				writeCandidates(k);
			} else {
				stop = true;
			}
			k++;
			Runtime.getRuntime().freeMemory();
		} while (!stop);
	}

	/**
	 * go through the database one by one to count each pattern. One pass
	 * 
	 * @param new_patterns
	 * @return
	 */
	private void count(Map<String, Double> new_patterns) {
		// if (new_patterns.size() > 10000) {
		// countByPattern(new_patterns, result);
		// } else {
		for (String one_transaction : database) {
			for (String np : new_patterns.keySet()) {
				if (contains(one_transaction, np)) {
					new_patterns.put(np, new_patterns.get(np) + 1);
				}
			}
		}
		// }
	}

	private void countByPattern(Set<String> new_patterns, Map<String, Double> result) {
		for (String np : new_patterns) {
			for (String one_transaction : database) {
				if (contains(one_transaction, np)) {
					if (result.containsKey(np)) {
						result.put(np, result.get(np) + 1);
					} else {
						result.put(np, 1.0);
					}
					break;
				}
			}
		}
	}

	private boolean contains(final String biggerS, final String smallS) {
		String[] bs = biggerS.trim().split(" ");
		String[] ss = smallS.trim().split(" ");
		int sindex = 0;
		for (int i = 0; i < bs.length; i++) {
			if (sindex < ss.length && bs[i].equals(ss[sindex])) {
				sindex++;
			} else if (sindex == ss.length) {
				return true;
			}
		}
		// if (sindex == ss.length) {
		// return true;
		// }
		return false;
	}

	/**
	 * generate the kth round candidates.
	 * 
	 * @param k
	 * @return
	 */
	private Map<String, Double> generate(int k) {
		// System.out.println("k=" + k + " in generate: " +
		// candidate_k_1.size());
		Map<String, Double> npatterns = new HashMap<String, Double>();
		current_candidates = new HashMap<String, Double>();
		for (String p : all_candidates) {
			for (String pn : all_candidates) {
				String new_pattern = p + SEPERATOR + pn;
				// System.out.println(new_pattern + " "
				// + new_pattern.split(SEPERATOR).length);
				if (!all_candidates.contains(new_pattern) && !npatterns.containsKey(new_pattern)
						&& new_pattern.split(SEPERATOR).length == k + 1) {
					npatterns.put(new_pattern, 0.0);
				}
			}
		}
		System.out.println(k + "-length patterns: " + npatterns.size());
		return npatterns;
	}
}
