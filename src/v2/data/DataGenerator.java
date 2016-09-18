package v2.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import activity.Activity;
import activity.ActivityUtil;
import concept.ConceptUtil;
import segmentation.ActivitySensorAssociation;
import segmentation.ActivitySensorEventUtil;
import sensor.SensorUtil;
import source.washington.WashingtonInfo;

public class DataGenerator {

	final private static String ADDR = "../Datasets/";
	final private static int MAX_SIZE_OF_SENSOREVENTS = 10;

	private ConceptUtil my_locations;
	private SensorUtil my_sensors;
	private ActivityUtil my_activities;

	public DataGenerator() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(ADDR + WashingtonInfo.META_DATA);
		ObjectInputStream ois = new ObjectInputStream(fis);
		my_locations = (ConceptUtil) ois.readObject();
		my_sensors = (SensorUtil) ois.readObject();
		my_activities = (ActivityUtil) ois.readObject();
		ois.close();
		fis.close();
	}

	public void splitIntoAct(final String association_file, final String output_dir, final boolean split2smaller)
			throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(association_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> segs = (List<ActivitySensorAssociation>) ois.readObject();
		ois.close();
		fis.close();
		if (split2smaller) {
			startSplitting(ActivitySensorEventUtil.splitBySize(MAX_SIZE_OF_SENSOREVENTS, segs), output_dir);
		} else {
			startSplitting(segs, output_dir);
		}
	}

	private void startSplitting(final List<ActivitySensorAssociation> segs, final String output_dir)
			throws IOException {
		for (Activity a : my_activities.getActivities()) {
			List<ActivitySensorAssociation> col = new ArrayList<ActivitySensorAssociation>();
			for (ActivitySensorAssociation asa : segs) {
				if (asa.getActEvent().getSensorId() == a.getActId()) {
					col.add(asa);
				}
			}
			File d = new File(output_dir + a.getActId());
			if (!d.exists()) {
				d.mkdirs();
				FileOutputStream fos = new FileOutputStream(d + "/f");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(col);
				oos.flush();
				oos.close();
				fos.close();
			}
		}
	}
	
	public void testOutput(final String output_file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(output_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> list = (List<ActivitySensorAssociation>)ois.readObject();
		for(ActivitySensorAssociation asa: list) {
			asa.print();
		}
	}

	// split into 10 files
	public void split2folders(final String act_dir,final int numOfFolders) throws IOException, ClassNotFoundException {
		for(Activity a: my_activities.getActivities()) {
			// read in
			FileInputStream fis = new FileInputStream(act_dir+a.getActId()+"/f");
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<ActivitySensorAssociation> list = (List<ActivitySensorAssociation>)ois.readObject();
			//shuffle
			Collections.shuffle(list);
			// initialise ten folders
			List<List<ActivitySensorAssociation>> result = DataUtil.initialiseFolders(numOfFolders);
			for(int i=0; i<list.size(); i++) {
				result.get(i%numOfFolders).add(list.get(i));
			}
			//write out
			for(int i=0; i< numOfFolders; i++) {
				FileOutputStream fos = new FileOutputStream(act_dir+a.getActId()+"/f"+i);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(result.get(i));
				oos.flush();
				oos.close();
				fos.close();
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		DataGenerator dg = new DataGenerator();
		// split into each activity
//		dg.splitIntoAct(Parameters.RAW_MAPPING, Parameters.ACT_SPLIT_DIR, false);
		// split into smaller size of sensor events for each activity
//		dg.splitIntoAct(Parameters.RAW_MAPPING, Parameters.ACT_SPLIT_MIN_DIR, true);
		//split into folders
		dg.split2folders(Parameters.ACT_SPLIT_DIR, Parameters.NUM_OF_FOLDERS);
		dg.split2folders(Parameters.ACT_SPLIT_MIN_DIR, Parameters.NUM_OF_FOLDERS);
		dg.testOutput(Parameters.ACT_SPLIT_MIN_DIR+"0/f2");
	}

}
