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
		for (Classifier cls : CLASSIFIERS) {
			for (int i = 0; i < TestWekaFileGenerator.k; i++) {
				System.out.println("test on "+i);
				WekaInferer wi = new WekaInferer(cls, FileAddresses.RAW_TRAIN + i + ".arff",
						FileAddresses.RAW_TEST + i + ".arff");
				wi.run();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}
}
