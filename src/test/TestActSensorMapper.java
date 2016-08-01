package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import parameters.FileAddresses;
import pattern.ActivitySensorMapper;
import pattern.ActivitySensorMapper2;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;

public class TestActSensorMapper {

	private TestDataImporter my_data;

	public TestActSensorMapper() throws ClassNotFoundException, IOException {
		my_data = new TestDataImporter();
	}

	
	public void map2() throws IOException {
		ActivitySensorMapper2 asm = new ActivitySensorMapper2(my_data.my_activities, my_data.my_sensors);
		List<ActivitySensorAssociation> result = asm.map(my_data.my_sensor_events, my_data.my_act_events);
		System.out.println("size: "+result.size());
		result.get(0).print();
		System.out.println("#######");
		result.get(result.size()-1).print();
		System.out.println("#######");
		result.get(result.size()/2).print();
	}
	public void map() throws IOException, ClassNotFoundException {
		ActivitySensorMapper asm = new ActivitySensorMapper(
				my_data.my_activities, my_data.my_sensors);
		// retrieve the segmented sensor events
		FileInputStream fis = new FileInputStream(FileAddresses.SE_SEG);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<List<SensorEvent>> segs = (List<List<SensorEvent>>) ois
				.readObject();
		ois.close();
		fis.close();
		List<ActivitySensorAssociation> result = asm.map(segs,
				my_data.my_act_events);
		System.out.println("size: " + result.size());
		result.get(0).print();
		result.get(result.size() - 1).print();
		result.get(result.size() / 2).print();
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		TestActSensorMapper tasm = new TestActSensorMapper();
//		tasm.map();
		tasm.map2();
		
	}
}
