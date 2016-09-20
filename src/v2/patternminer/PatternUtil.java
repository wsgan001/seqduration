package v2.patternminer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import parameters.Symbols;

public class PatternUtil {

	public static Map<Integer, List<String>> getDB(final String the_db_addr, final int[] acts)
			throws FileNotFoundException {
		Map<Integer, List<String>> reuslt = new HashMap<Integer, List<String>>();
		for (int a : acts) {
			List<String> db = PatternUtil.getDB(the_db_addr + a);
			if (db != null) {
				reuslt.put(a, db);
			}
		}
		return reuslt;
	}

	public static List<String> getDB(final String the_db_file) {
		File f = new File(the_db_file);
		try {
			Scanner sc = new Scanner(f);
			List<String> database = new ArrayList<String>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.isEmpty()) {
					database.add(line);
				}
			}
			return database;
		} catch (FileNotFoundException e) {
			System.out.println(the_db_file + " does not exist");
			return null;
		}
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
	
	public static int getTotal(int[] acts) {
		int result = 0;
		for(int a: acts) {
			result+=a;
		}
		return result;
	}
}
