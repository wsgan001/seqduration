package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import parameters.FileAddresses;
import pattern.PatternMapper;
import segmentation.ActivitySensorAssociation;

public class TestPatternMapper {

	public static void run() throws IOException, ClassNotFoundException {
		PatternMapper pm = new PatternMapper();
		FileInputStream fis = new FileInputStream(FileAddresses.SE_SEG);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> input = (List<ActivitySensorAssociation>) ois
				.readObject();
		pm.write(input);
	}
	
	public static void getMapping() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(FileAddresses.MAP_SEQ_DURATION_INDEX);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<String> map = (List<String>)ois.readObject();
		ois.close();
		fis.close();
		System.out.println(map);
	}
	
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		run();
//		getMapping();
	}

}
