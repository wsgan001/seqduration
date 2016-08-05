package test;

import parameters.FileAddresses;
import weka.WekaInferer;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class TestWeka {

	final private static Classifier[] CLASSIFIERS = { new RandomForest(), new NaiveBayes(), new J48() };

	public static void run() throws Exception {
		double[] accuracy = new double[CLASSIFIERS.length];
		int c = 0;
		for (Classifier cls : CLASSIFIERS) {
			for (int i = 0; i < TestWekaFileGenerator.k; i++) {
				WekaInferer wi = new WekaInferer(cls, FileAddresses.SPLIT_TRAIN + i + ".arff",
						FileAddresses.SPLIT_TEST + i + ".arff");
				accuracy[c] += wi.run();
//				System.out.println("test on " + i+": "+accuracy[c]);
			}
			accuracy[c] /= (1.0*TestWekaFileGenerator.k);
			System.out.println("classifier "+c+": "+accuracy[c]);
			c++;
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}
}
