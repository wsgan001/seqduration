package v2.data;

import java.util.ArrayList;
import java.util.List;

import segmentation.ActivitySensorAssociation;

public class DataUtil {

	public static List<List<ActivitySensorAssociation>> initialiseFolders(final int numOfFolders) {
		List<List<ActivitySensorAssociation>> result = new ArrayList<List<ActivitySensorAssociation>>();
		for(int i=0; i< numOfFolders; i++) {
			result.add(new ArrayList<ActivitySensorAssociation>());
		}
		return result;
	}
	
	public static String generateGroupFileName(final String dir, final int[] acts) {
		String dir_name = dir;
		for (int a = 0; a < acts.length; a++) {
			dir_name += "_" + acts[a];
		}
		return dir_name;
	}
}
