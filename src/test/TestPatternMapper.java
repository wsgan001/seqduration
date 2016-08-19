package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import parameters.FileAddresses;
import patterncreater.PatternMapper;
import segmentation.ActivitySensorAssociation;

public class TestPatternMapper {

	public static void run(final String the_asa, final String the_sq, final String the_indices,
			final String the_db_output) throws IOException, ClassNotFoundException {
		PatternMapper pm = new PatternMapper(the_sq, the_indices);
		FileInputStream fis = new FileInputStream(the_asa);
		// (FileAddresses.SE_SEG);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> input = (List<ActivitySensorAssociation>) ois.readObject();
		pm.write(input, the_db_output);
	}

	public static void getMapping() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(FileAddresses.MAP_SEQ_DURATION_INDEX);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<String> map = (List<String>) ois.readObject();
		ois.close();
		fis.close();
		System.out.println(map);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		for (int i = 0; i < 10; i++) {
			run(FileAddresses.RAW_TRAIN_ASA + i, FileAddresses.RAW_TRAIN_MAP_SQ + i,
					FileAddresses.RAW_TRAIN_MAP_INDICES + i, FileAddresses.RAW_TRAIN_DB + i);
		}
		// getMapping();
	}

}
