package test;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import activity.ActivityUtil;
import concept.ConceptUtil;
import parameters.FileAddresses;
import patterninference.InfererAbs;
import segmentation.ActivitySensorAssociation;
import sensor.Sensor;
import sensor.SensorEvent;
import sensor.SensorUtil;
import source.washington.WashingtonInfo;

public class TestInferer {

	private static String RESULT_ADDR = "results/t_";

	public static SensorUtil getSensors() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream("../Datasets/" + WashingtonInfo.META_DATA);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ois.readObject();
		SensorUtil my_sensors = (SensorUtil) ois.readObject();
		ois.close();
		fis.close();
		return my_sensors;
	}

	private static void testSensorUtil(SensorUtil the_sensors) {
		// print out
		for(Sensor si: the_sensors.getSensors()) {
			System.out.println(si.getId()+": "+si.getLocation()+"");
		}
		// similarity
		System.out.println(the_sensors.getLocation().similarity(the_sensors.findSensor(25).getLocation(), the_sensors.findSensor(38).getLocation()));
	}
	
	public static void run() throws ClassNotFoundException, IOException {
		final SensorUtil my_sensors = getSensors();
//		System.out.println("test similarity: "+my_sensors.similarity(my_sensors.findSensor(25), my_sensors.findSensor(34)));
		for (int i = 0; i < 1; i++) {
			List<ActivitySensorAssociation> test_data = TestDataGenerator.getData(FileAddresses.RAW_FOLD + i);
			FileWriter fw = new FileWriter(RESULT_ADDR + i);
			// prepare inferer
			InfererAbs ia = new InfererAbs(my_sensors, fw, FileAddresses.RAW_TRAIN_PATTERN_SCORE + i + "_", 25,
					FileAddresses.RAW_TRAIN_MAP_SQ + i, FileAddresses.RAW_TRAIN_MAP_INDICES + i);
			int c = 0;
			for (ActivitySensorAssociation asa : test_data) {
				if (c++ > 3) {
					break;
				}
				writeData(fw, asa);
				double[] in = ia.infer(asa.getSensorEvents());
				writeResult(fw, in);
			}
			fw.close();
		}
	}

	private static void writeData(FileWriter fw, ActivitySensorAssociation asa) throws IOException {
		fw.write(asa.getActEvent().getSensorId() + ":");
		for (SensorEvent se : asa.getSensorEvents()) {
			fw.write(se.getSensorId() + ",");
		}
		fw.write("\n");
	}

	private static void writeResult(FileWriter fw, double[] inferred) throws IOException {
		double max = Double.NEGATIVE_INFINITY;
		int a = -1;
		for (int i = 0; i < inferred.length; i++) {
//			fw.write(inferred[i] + ",");
			if (inferred[i] > max) {
				max = inferred[i];
				a = i;
			}
		}
		fw.write("==>" + a + "\n");
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		run();
//		testSensorUtil(getSensors());
	}

}
