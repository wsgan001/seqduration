package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import datagenerator.DataGenerator;
import parameters.FileAddresses;
import segmentation.ActivitySensorAssociation;

public class TestDataGenerator {

	final private static int NUMOFACT = 25;

	protected static Set<Integer> getActivities() {
		Set<Integer> result = new HashSet<Integer>();
		for (int a = 0; a < NUMOFACT; a++) {
			if (a != 9 && a != 20 && a != 18) {
				result.add(a);
			}
		}
		return result;
	}

	public static List<ActivitySensorAssociation> getData(final String the_file)
			throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(the_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> result = (List<ActivitySensorAssociation>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println("size: " + result.size());
		return result;
	}

	public static void start(final String the_output, final String the_input)
			throws ClassNotFoundException, IOException {
		List<ActivitySensorAssociation> data = getData(the_input);
		DataGenerator dg = new DataGenerator(data, getActivities(), the_output);
		dg.separate();
	}

	public static void generateTrain(final String the_input, final String the_output)
			throws ClassNotFoundException, IOException {
		for (int i = 0; i < 10; i++) {
			List<ActivitySensorAssociation> trainingData = new ArrayList<ActivitySensorAssociation>();
			for (int ki = 0; ki < 10; ki++) {
				if (ki != i) {
					{
						FileInputStream fis = new FileInputStream(the_input + ki);
						ObjectInputStream ois = new ObjectInputStream(fis);
						List<ActivitySensorAssociation> list = (List<ActivitySensorAssociation>) ois.readObject();
						ois.close();
						fis.close();
						trainingData.addAll(list);
					}
				}
			}
			{
				FileOutputStream fos = new FileOutputStream(the_output + i);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(trainingData);
				oos.close();
				fos.close();
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// start(FileAddresses.RAW_FOLD, FileAddresses.RAW_ASSOCIATION);
		// start(FileAddresses.SPLIT_FOLD, FileAddresses.SPLIT_ASSOCIATION);
		generateTrain(FileAddresses.RAW_FOLD, FileAddresses.RAW_TRAIN_ASA);
	}
}
