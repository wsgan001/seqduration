package v2.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import segmentation.ActivitySensorAssociation;

/**
 * group activities for examine
 * 
 * @author juan
 *
 */
public class ActGrouping {
	/**
	 * activities that we are interested to examine
	 */
	private int[] my_actsOfInterest;

	private List<List<ActivitySensorAssociation>> my_trainingData;

	private List<List<ActivitySensorAssociation>> my_testData;

	public ActGrouping(final int[] the_actsOfInterest) {
		my_actsOfInterest = the_actsOfInterest;
		my_trainingData = DataUtil.initialiseFolders(Parameters.NUM_OF_FOLDERS);
		my_testData = DataUtil.initialiseFolders(Parameters.NUM_OF_FOLDERS);
	}

	public void start(final String dir) throws IOException, ClassNotFoundException {
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			for (int a = 0; a < my_actsOfInterest.length; a++) {
				FileInputStream fis = new FileInputStream(dir + my_actsOfInterest[a] + "/f" + i);
				ObjectInputStream ois = new ObjectInputStream(fis);
				List<ActivitySensorAssociation> l = (List<ActivitySensorAssociation>) ois.readObject();
				my_testData.get(i).addAll(l);
			}
			for (int a = 0; a < my_actsOfInterest.length; a++) {
				for (int j = 0; j < Parameters.NUM_OF_FOLDERS; j++) {
					if (j != i) {
						FileInputStream fis = new FileInputStream(dir + my_actsOfInterest[a] + "/f" + j);
						ObjectInputStream ois = new ObjectInputStream(fis);
						List<ActivitySensorAssociation> l = (List<ActivitySensorAssociation>) ois.readObject();
						my_trainingData.get(i).addAll(l);
					}
				}
			}
		}
	}

	public void serialise(final String dir, final String appendix) throws IOException {
		// create a folder
		String dir_name = DataUtil.generateGroupFileName(dir, my_actsOfInterest);
		File d = new File(dir_name);
		if (!d.exists()) {
			d.mkdir();
		}
		FileOutputStream fos = new FileOutputStream(dir_name + "/" + appendix);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(my_trainingData);
		oos.writeObject(my_testData);
		oos.flush();
		oos.close();
		fos.close();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ActGrouping ag = new ActGrouping(Parameters.R1_ROOM);
		ag.start(Parameters.ACT_SPLIT_MIN_DIR);
		ag.serialise(Parameters.GROUPS_DIR, Parameters.SPLIT);
	}

}
