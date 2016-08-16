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

public class SequentialMiner {

	final private static int MAX_LENGTH = 8;

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

	public Map<String, Set<Integer>> all_candidates;

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

	public SequentialMiner(final String the_db_file, final String the_cand_file, final double the_support_count,
			final int the_top_k) throws FileNotFoundException {
		my_db_file = the_db_file;
		my_candidate_file = the_cand_file;
		getDB(the_db_file);
		support_count = the_support_count * database.size();
		current_candidates = new HashMap<String, Double>();
		all_candidates = new HashMap<String, Set<Integer>>();
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
		MapUtil.orderAndWrite(current_candidates, fw, my_top_k, database.size());
		fw.close();
		// all_candidates.addAll(current_candidates.keySet());
		current_candidates = new HashMap<String, Double>();
	}

	private Set<String> getUniqueItems(final String line) {
		String[] items = line.split(SEPERATOR);
		Set<String> result = new HashSet<String>();
		for (String i : items) {
			result.add(i);
		}
		return result;
	}

	private void oneItemCandidate() throws IOException {
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		for (int i = 0; i < database.size(); i++) {
			Set<String> items = getUniqueItems(database.get(i));
			for (String item : items) {
				if (!map.containsKey(item)) {
					map.put(item, new HashSet<Integer>());
				}
				map.get(item).add(i);
			}
		}
		for (String s : map.keySet()) {
			if (map.get(s).size() >= support_count) {
				all_candidates.put(s, map.get(s));
				current_candidates.put(s, map.get(s).size() * 1.0);
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
			generate(k);
			if (!current_candidates.isEmpty()) {
				writeCandidates(k);
			} else {
				stop = true;
			}
			k++;
			Runtime.getRuntime().freeMemory();
		} while (!stop || k < MAX_LENGTH);
	}

	private void generate(int k) {
		Map<String, Set<Integer>> result = new HashMap<String, Set<Integer>>();
		for (String l : all_candidates.keySet()) {
			for (String r : all_candidates.keySet()) {
				String p = l + SEPERATOR + r;
				Set<Integer> indices = new HashSet<Integer>();
				if (p.split(SEPERATOR).length == k + 1 && !all_candidates.containsKey(p)) {
					for (int li : all_candidates.get(l)) {
						if (all_candidates.get(r).contains(li) && contains(database.get(li), p)) {
							indices.add(li);
						}
					}
				}
				if (indices.size() >= support_count) {
					current_candidates.put(p, indices.size() * 1.0);
					result.put(p, indices);
				}
			}
		}
		all_candidates.putAll(result);
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

}
