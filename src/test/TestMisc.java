package test;

import java.util.HashMap;
import java.util.Map;

public class TestMisc {

	private static boolean contains(final String biggerS, final String smallS) {
		String[] bs = biggerS.trim().split(" ");
		String[] ss = smallS.trim().split(" ");
		int sindex = 0;
		for (int i = 0; i < bs.length; i++) {
			if (sindex < ss.length && bs[i].equals(ss[sindex])) {
				System.out.println("found: "+sindex);
				sindex++;
			}
		}
		System.out.println("small index: "+sindex);
		if (sindex == ss.length) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String s1 = "210 103 100 ";
		String s2 = "10";
		System.out.println(contains(s2, s2));

		// Map<String, Double> map = new HashMap<String, Double>();
		// map.put(s2, 1.0);
		// System.out.println(map.containsKey("10 103"));

	}

}
