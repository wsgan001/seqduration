package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import parameters.FileAddresses;
import segmentation.ActivitySensorAssociation;
import weka.WekaFileGenerator;

public class TestWekaFileGenerator {

	final private static int my_numOfSensors = 83;

	final protected static int k = 10;

	private List<ActivitySensorAssociation> my_trainingData;

	private List<ActivitySensorAssociation> my_testData;

	public void run(final String input_addr, final String output_train_addr, final String output_test_addr, final int i)
			throws ClassNotFoundException, IOException {
		Set<Integer> acts = TestDataGenerator.getActivities();
		WekaFileGenerator wfg = new WekaFileGenerator(my_numOfSensors, acts);
		separate(input_addr, i);
		wfg.generate(output_train_addr+i, my_trainingData);
		wfg.generate(output_test_addr+i, my_testData);
	}

	private void separate(final String input_addr, int i) throws IOException, ClassNotFoundException {
		my_trainingData = new ArrayList<ActivitySensorAssociation>();
		{
			FileInputStream fis = new FileInputStream(input_addr + i);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_testData = (List<ActivitySensorAssociation>) ois.readObject();
			ois.close();
			fis.close();
		}
		{
			for (int ki = 0; ki < k; ki++) {
				if (ki != i) {
					{
						FileInputStream fis = new FileInputStream(input_addr + ki);
						ObjectInputStream ois = new ObjectInputStream(fis);
						List<ActivitySensorAssociation> list = (List<ActivitySensorAssociation>) ois.readObject();
						ois.close();
						fis.close();
						my_trainingData.addAll(list);
					}
				}
			}
		}
	}

	public static void run() throws ClassNotFoundException, IOException {
		for (int i = 0; i < k; i++) {
			TestWekaFileGenerator twfg = new TestWekaFileGenerator();
			twfg.run(FileAddresses.RAW_FOLD, FileAddresses.RAW_TRAIN, FileAddresses.RAW_TEST, i);
//			twfg.run(FileAddresses.SPLIT_FOLD, FileAddresses.SPLIT_TRAIN, FileAddresses.SPLIT_TEST, i);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		run();
	}

}
