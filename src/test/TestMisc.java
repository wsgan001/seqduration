package test;

import java.util.HashMap;
import java.util.Map;

public class TestMisc {
	
	private static double log(double v, double base) {
		return Math.log(v)/Math.log(base);
	}
	
	private static void testIG(double x) {
		double a = -log(x,2) * x;
		double b = -log(1-x,2)*(1-x);
		System.out.println(a+" + "+b+" = "+(a+b));
//		System.out.println(Math.log(0.3333333)/Math.log(3));
		System.out.println(a/(a+b)+","+b/(a+b));
	}

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
//		String s1 = "210 103 100 ";
//		String s2 = "10";
////		System.out.println(contains(s2, s2));
////		System.out.println(Math.ceil(0.6));
//		String s3 = "a1,a2,";
//		s3 = s3.substring(0, s3.length()-1);
//		System.out.println(s3);
		System.out.println(Math.pow(Math.E, 0.36));
		// Map<String, Double> map = new HashMap<String, Double>();
		// map.put(s2, 1.0);
		// System.out.println(map.containsKey("10 103"));

	}

}
