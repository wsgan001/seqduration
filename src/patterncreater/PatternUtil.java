package patterncreater;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;

public class PatternUtil {

	public static Map<Integer, List<String>> getDB(final String the_db_addr, final int numOfActs)
			throws FileNotFoundException {
		Map<Integer, List<String>> reuslt = new HashMap<Integer, List<String>>();
		for (int i = 0; i < numOfActs; i++) {
			List<String> db = PatternUtil.getDB(the_db_addr + i);
			if (db != null) {
				reuslt.put(i, db);
			}
		}
		return reuslt;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int map2cluster(final List<DescriptiveStatistics> clusters,
			final double duration) {
		double min_dist = 0;
		int clusterID = -1;
		for (int i = 0; i < clusters.size(); i++) {
			if (clusterID < 0
					|| Math.abs(clusters.get(i).getMean() - duration) < min_dist) {
				clusterID = i;
			}
		}
		return clusterID;
	}

	public static List<String> getDB(final String the_db_file) {
		File f = new File(the_db_file);
		try {
			Scanner sc = new Scanner(f);
			List<String> database = new ArrayList<String>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String s = "";
				String[] split = line.split(Symbols.DB_SEPARATOR);
				for (int i = 0; i < split.length; i++) {
					if (!split[i].contains("-") && !split[i].isEmpty()) {
						s += split[i] + Symbols.DB_SEPARATOR;
					}
				}
				if (!s.isEmpty())
					database.add(s);
			}
			return database;
		} catch (FileNotFoundException e) {
			System.out.println(the_db_file + " does not exist");
			return null;
		}
	}

	/**
	 * retrieve unique sequence pattern ids from one line of a db file
	 * 
	 * @param line
	 * @return
	 */
	public static Set<String> getUniqueItems(final String line) {
		String[] items = line.split(Symbols.DB_SEPARATOR);
		Set<String> result = new HashSet<String>();
		for (String i : items) {
			result.add(i);
		}
		return result;
	}

	/**
	 * one pattern contains another one or not?
	 * 
	 * @param biggerS
	 *            a string composed of 2-length sequence pattern ids, separated
	 *            by a space.
	 * @param smallS
	 * @return
	 */
	public static boolean contains(final String biggerS, final String smallS) {
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
