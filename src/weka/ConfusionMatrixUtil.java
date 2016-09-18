package weka;

import java.text.DecimalFormat;

public class ConfusionMatrixUtil {
	
	final private static DecimalFormat df = new DecimalFormat("#.##");

	public static void add(double[][] added, double[][] addTo) {
		for (int i = 0; i < addTo.length; i++) {
			for (int j = 0; j < addTo[0].length; j++) {
				added[i][j] += addTo[i][j];
			}
		}
	}

	public static void normaliseByColumn(double[][] matrix) {
		double[] column_sum = new double[matrix[0].length];
		for (int j = 0; j < matrix[0].length; j++) {
			for (int i = 0; i < matrix.length; i++) {
				column_sum[j] += matrix[i][j];
			}
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] /= column_sum[j];
			}
		}
	}

	public static void normaliseByRow(double[][] matrix) {
		double[] row_sum = new double[matrix.length];
		for (int j = 0; j < matrix.length; j++) {
			for (int i = 0; i < matrix[0].length; i++) {
				row_sum[j] += matrix[j][i];
			}
		}
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] /= row_sum[i];
			}
		}
	}
	
	public static void print(double[][] matrix) {
		for(int i=0; i< matrix.length; i++) {
			for(int j=0; j< matrix[0].length; j++) {
				System.out.print(df.format(matrix[i][j])+"\t");
			}
			System.out.println();
		}
	}

}
