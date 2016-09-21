package test;

import java.text.DecimalFormat;

public class Print {

	private static DecimalFormat df = new DecimalFormat("#.##");
	public static void printArray(double[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(df.format(array[i]) + "\t");
		}
	}
	
	public static void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + "\t");
		}
	}

	public static void printArray(double[][] array) {
		for (int i = 0; i < array.length; i++) {
			printArray(array[i]);
			System.out.print("\n");
		}
	}
}
