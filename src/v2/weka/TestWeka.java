package v2.weka;

import java.util.ArrayList;
import java.util.List;

import parameters.FileAddresses;
import v2.data.DataUtil;
import v2.data.Parameters;
import weka.ConfusionMatrixUtil;
import weka.WekaInferer;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class TestWeka {

	final private static Classifier[] CLASSIFIERS = { new RandomForest(), new NaiveBayes(), new J48() };

	public static void run(final String weka_file) throws Exception {
		double[] accuracy = new double[CLASSIFIERS.length];
		int c = 0;
		for (Classifier cls : CLASSIFIERS) {
			double[][] cm = null;
			for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
				WekaInferer wi = new WekaInferer(cls, weka_file + "tr" + i + ".arff", weka_file + "te" + i + ".arff");
				wi.run();
				accuracy[c] += wi.getAccuracies();
				if (cm == null) {
					cm = wi.getConfusionMatrix();
				} else {
					ConfusionMatrixUtil.add(cm, wi.getConfusionMatrix());
				}
				// System.out.println("test on " + i+": "+accuracy[c]);
			}
			accuracy[c] /= (1.0 * Parameters.NUM_OF_FOLDERS);
			System.out.println("classifier " + cls.getClass().getSimpleName() + ": " + accuracy[c]);
			ConfusionMatrixUtil.normaliseByRow(cm);
			ConfusionMatrixUtil.print(cm);
			c++;
		}
	}

	public static void main(String[] args) throws Exception {
		run(DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, Parameters.R1_ROOM)+"/source_weka_");
	}
}
