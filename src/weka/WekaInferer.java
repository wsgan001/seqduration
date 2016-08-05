package weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaInferer {

	private Classifier my_classifier;
	private String my_train_file;
	private String my_test_file;
	private double[][] my_confusionMatrix;
	private double my_correct;

	public WekaInferer(Classifier the_classifier, String the_train_file, String the_test_file) {
		my_classifier = the_classifier;
		my_train_file = the_train_file;
		my_test_file = the_test_file;
	}

	private Instances getData(final String the_arff_file) throws Exception {
		DataSource ds = new DataSource(the_arff_file);
		return ds.getDataSet();
	}

	public void run() throws Exception {
		Instances trainData = getData(my_train_file);
		if (trainData.classIndex() == -1)
			trainData.setClassIndex(trainData.numAttributes() - 1);
		Instances testData = getData(my_test_file);
		if (testData.classIndex() == -1)
			testData.setClassIndex(testData.numAttributes() - 1);

		Evaluation eval = new Evaluation(trainData);
		my_classifier.buildClassifier(trainData);
		eval.evaluateModel(my_classifier, testData);
		// System.out.println(eval.correct()/testData.size()+":
		// \n"+eval.toSummaryString());
		my_correct = eval.correct() / testData.size();
		my_confusionMatrix = eval.confusionMatrix();
	}

	public double getAccuracies() {
		return my_correct;
	}

	public double[][] getConfusionMatrix() {
		return my_confusionMatrix;
	}

}
