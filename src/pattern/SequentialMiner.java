package pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SequentialMiner {

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

	public List<Map<String, Double>> candidates;

	/**
	 * Map<a string consisting of ids of the previous-k situations, Map<id for
	 * the next situation, probability>>
	 */
	// private Map<String, Map<String, Double>> model;

	// /**
	// * record how many times the previous k locations occur.
	// */
	// private Map<String, Integer> occurrences;

	public SequentialMiner(final String the_db_file,
			final double the_support_count) throws FileNotFoundException {
		getDB(the_db_file);
		support_count = the_support_count * database.size();
		candidates = new ArrayList<Map<String, Double>>();
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

	private Map<String, Double> oneItemCandidate() {
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
		Map<String, Double> cands = new HashMap<String, Double>();
		for (String cand : result.keySet()) {
			if (cand != null && !cand.isEmpty()
					&& result.get(cand) >= support_count) {
				cands.put(cand, result.get(cand));
				// System.out.println(cand+" "+result.get(cand));
			}
		}
		candidates.add(0, cands);
		return result;
	}

	public void buildInternalModel() {
		oneItemCandidate();
		int k = 1;
		Map<String, Double> round_k = null;
		do {
			System.out.println("pattern round k: "+k);
			round_k = new HashMap<String, Double>();
			// generate the kth round pattern candidates
			Set<String> patterns = generate(k);
			for (String p : patterns) {
				double sc = updateSupportCount(p);
				if (sc > support_count) {
					round_k.put(p, sc);
				}
			}
			if (!round_k.isEmpty()) {
				candidates.add(k, round_k);
			}
			k++;
		} while (!round_k.isEmpty());
	}

	/**
	 * update support count if a pattern is supported by the sequences
	 * 
	 * @param a_candidate
	 * @return
	 */
	private double updateSupportCount(String a_candidate) {
		double count = 0;
		for (String one_transaction : database) {
			// String converted = Utility.form(one_transaction);
			if (contains(one_transaction, a_candidate)) {
				count = count + 1;
			}
		}
		return count;
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
//		if (sindex == ss.length) {
//			return true;
//		}
		return false;
	}

	/**
	 * generate the kth round candidates.
	 * 
	 * @param k
	 * @return
	 */
	private Set<String> generate(int k) {
		int k2 = k - 1;
		Set<String> candidate_k_1 = new HashSet<String>();
		while (k2 >= 0) {
			for (String s : candidates.get(k2).keySet()) {
				candidate_k_1.add(s);
			}
			k2--;
		}

		// System.out.println("k=" + k + " in generate: " +
		// candidate_k_1.size());
		Set<String> patterns = new HashSet<String>();
		for (String p : candidate_k_1) {
			for (String pn : candidate_k_1) {
				String new_pattern = p + SEPERATOR + pn;
				// System.out.println(new_pattern + " "
				// + new_pattern.split(SEPERATOR).length);
				if (!candidate_k_1.contains(new_pattern)
						&& new_pattern.split(SEPERATOR).length == k + 1) {
					patterns.add(new_pattern);
				}
			}
		}
		 System.out.println(k + "-length patterns: " + patterns.size());
		return patterns;
	}
}
