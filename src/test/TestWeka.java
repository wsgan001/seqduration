package test;

import java.util.ArrayList;
import java.util.List;

import parameters.FileAddresses;
import weka.ConfusionMatrixUtil;
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
			double[][] cm = null;
			for (int i = 0; i < TestWekaFileGenerator.k; i++) {
				WekaInferer wi = new WekaInferer(cls, FileAddresses.RAW_TRAIN + i + ".arff",
						FileAddresses.RAW_TEST + i + ".arff");
				wi.run();
				accuracy[c] += wi.getAccuracies();
				if (cm ==null) {
					cm = wi.getConfusionMatrix();
				} else {
					ConfusionMatrixUtil.add(cm, wi.getConfusionMatrix());
				}
//				System.out.println("test on " + i+": "+accuracy[c]);
			}
			accuracy[c] /= (1.0*TestWekaFileGenerator.k);
			System.out.println("classifier "+c+": "+accuracy[c]);
			ConfusionMatrixUtil.normaliseByRow(cm);
			ConfusionMatrixUtil.print(cm);
			c++;
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}
}
