package v2.weka;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import v2.data.DataUtil;
import v2.data.Parameters;

public class WekaFileGenerator {

	private int my_numOfSensors;
	private int[] my_acts;

	public WekaFileGenerator(final int the_numOfSensors, final int[] the_activities) {
		my_acts = the_activities;
		my_numOfSensors = the_numOfSensors;
	}

	public void generate(final String input_dir, final String appendix) throws IOException, ClassNotFoundException {
		final String group_dir = DataUtil.generateGroupFileName(input_dir, my_acts);
		// read train and test data
		FileInputStream fis = new FileInputStream(group_dir + "/" + appendix);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<List<ActivitySensorAssociation>> train = (List<List<ActivitySensorAssociation>>) ois.readObject();
		List<List<ActivitySensorAssociation>> test = (List<List<ActivitySensorAssociation>>) ois.readObject();
		ois.close();
		fis.close();
		// fore each pair of train and test, generate arff files
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			{
				Collections.shuffle(train.get(i));
				ARFFGenerator ag = new ARFFGenerator(my_numOfSensors, my_acts, train.get(i));
				ag.generate(group_dir + "/" + appendix + "_weka_tr" + i );
			}
			{
				Collections.shuffle(train.get(i));
				ARFFGenerator ag = new ARFFGenerator(my_numOfSensors, my_acts, test.get(i));
				ag.generate(group_dir + "/" + appendix + "_weka_te" + i);
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		WekaFileGenerator wfg = new WekaFileGenerator(83, Parameters.R1_ROOM);
		wfg.generate(Parameters.GROUPS_DIR, Parameters.SPLIT);
	}

}
