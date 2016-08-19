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
import parameters.Symbols;

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
	 * 
	 * @throws IOException
	 */
	// private Map<String, Map<String, Double>> model;

	// /**
	// * record how many times the previous k locations occur.
	// */
	// private Map<String, Integer> occurrences;

	public SequentialMiner(final String the_db_file, final String the_cand_file, final double the_support_count,
			final int the_top_k) throws IOException {
		my_db_file = the_db_file;
		my_candidate_file = the_cand_file;
		database = PatternUtil.getDB(the_db_file);
		my_top_k = the_top_k;
		if (!database.isEmpty()) {
			if (!prescreen()) {
				allTheSame();
			} else {
//				support_count = the_support_count * database.size();
//				current_candidates = new HashMap<String, Double>();
//				all_candidates = new HashMap<String, Set<Integer>>();
//				my_top_k = the_top_k;
//				buildInternalModel();
				shortcut();
			}
		}
	}

	/**
	 * check if all the instances in the db are identical.
	 */
	private boolean prescreen() {
		String s = database.get(0);
		boolean notSame = false;
		for (int i = 1; i < database.size(); i++) {
			if (!database.get(i).equals(s)) {
				notSame = true;
			}
		}
		return notSame;
	}

	private void allTheSame() throws IOException {
		String s = database.get(0);
		Set<String> unique = new HashSet<String>();
		String[] split = s.split(Symbols.DB_SEPARATOR);
		for (int k = 1; k <= split.length; k++) {
			FileWriter fw = new FileWriter(my_candidate_file + "." + k + FileAddresses.PATTERN_AFFIX);
			for (int i = 0; i < split.length - k; i++) {
				String p = "";
				for (int ki = i; ki < i + k; ki++) {
					p += split[ki] + Symbols.DB_SEPARATOR;
				}
				if (!unique.contains(p)) {
					fw.write(p + Symbols.PATTERN_SEPARATOR_IT + "1.0\n");
					unique.add(p);
				}

			}
			fw.close();
		}
	}

	private void shortcut() throws IOException {
		int k = 1;
		do {
			if (!retrieve(k)) {
				break;
			}
			count();
//			System.out.println(current_candidates);
			writeCandidates(k-1);
			k++;
		} while (true);
	}

	private boolean retrieve(final int lengthOfSeg) {
		current_candidates = new HashMap<String, Double>();
		for (int i = 0; i < database.size(); i++) {
			String[] split = database.get(i).split(Symbols.DB_SEPARATOR);
			for (int j = 0; j < split.length - lengthOfSeg; j++) {
				String p = "";
				for (int kj = j; kj < j + lengthOfSeg; kj++) {
					p += split[kj] + Symbols.DB_SEPARATOR;
				}
				if (!current_candidates.containsKey(p)) {
					current_candidates.put(p, 0.0);
				}
			}
		}
		if (current_candidates.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private void count() {
		for (int i = 0; i < database.size(); i++) {
			for (String p : current_candidates.keySet()) {
				if (PatternUtil.contains(database.get(i), p)) {
					double c = current_candidates.get(p) + 1;
					current_candidates.put(p, c);
				}
			}
		}
	}

	private void writeCandidates(int k) throws IOException {
		FileWriter fw = new FileWriter(my_candidate_file + "." + k + FileAddresses.PATTERN_AFFIX);
		MapUtil.orderAndWrite(current_candidates, fw, my_top_k, database.size());
		fw.close();
		// all_candidates.addAll(current_candidates.keySet());
		current_candidates = new HashMap<String, Double>();
	}

	private void oneItemCandidate() throws IOException {
		Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
		for (int i = 0; i < database.size(); i++) {
			Set<String> items = PatternUtil.getUniqueItems(database.get(i));
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
		} while (!stop && k < MAX_LENGTH);
	}

	private void generate(int k) {
		Map<String, Set<Integer>> result = new HashMap<String, Set<Integer>>();
		for (String l : all_candidates.keySet()) {
			for (String r : all_candidates.keySet()) {
				String p = l + SEPERATOR + r;
				Set<Integer> indices = new HashSet<Integer>();
				if (p.split(SEPERATOR).length == k + 1 && !all_candidates.containsKey(p)) {
					for (int li : all_candidates.get(l)) {
						if (all_candidates.get(r).contains(li) && PatternUtil.contains(database.get(li), p)) {
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

}
