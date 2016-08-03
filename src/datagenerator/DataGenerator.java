package datagenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parameters.FileAddresses;
import segmentation.ActivitySensorAssociation;

public class DataGenerator {
	/**
	 * all the input data
	 */
	private List<ActivitySensorAssociation> my_input;
	/**
	 * the activities of interest
	 */
	private Set<Integer> my_activities;

	final private static int k = 10;

	public DataGenerator(final List<ActivitySensorAssociation> the_data, final Set<Integer> the_activities) {
		my_input = the_data;
		my_activities = the_activities;
	}

	/**
	 * separate the data into 10 folds
	 * @throws IOException 
	 */
	public void separate() throws IOException {
		Map<Integer, List<List<Integer>>> folds = separateIntoKFolders(getAct2Indices());
		List<List<ActivitySensorAssociation>> result = new ArrayList<List<ActivitySensorAssociation>>();
		for (int i = 0; i < k; i++) {
			result.add(new ArrayList<ActivitySensorAssociation>());
		}
		for (int act : folds.keySet()) {
			for (int i = 0; i < folds.get(act).size(); i++) {
				for (int index : folds.get(act).get(i)) {
					result.get(i).add(my_input.get(index));
				}
			}
		}
		write(result);
	}
	
	private void write(List<List<ActivitySensorAssociation>> the_data) throws IOException {
		for(int i=0; i< the_data.size(); i++) {
			FileOutputStream fos = new FileOutputStream(FileAddresses.FOLD+i);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(the_data.get(i));
			oos.close();
			fos.close();
		}
	}

	/**
	 * separate activities by their indices in the data
	 * 
	 * @return
	 */
	private Map<Integer, List<Integer>> getAct2Indices() {
		Map<Integer, List<Integer>> result = new HashMap<Integer, List<Integer>>();
		for (int i : my_activities) {
			result.put(i, new ArrayList<Integer>());
		}
		for (int i = 0; i < my_input.size(); i++) {
			result.get(my_input.get(i).getActEvent().getSensorId()).add(i);
		}
		return result;
	}

	private Map<Integer, List<List<Integer>>> separateIntoKFolders(Map<Integer, List<Integer>> act2Indices) {
		Map<Integer, List<List<Integer>>> result = new HashMap<Integer, List<List<Integer>>>();
		for (int i : my_activities) {
			result.put(i, new ArrayList<List<Integer>>());
		}
		for (int a : act2Indices.keySet()) {
			List<Integer> set = act2Indices.get(a);
			Collections.shuffle(set);
			List<List<Integer>> list = result.get(a);
			List<Integer> l = new ArrayList<Integer>();
			for (int i = 0; i < set.size(); i++) {
				if (i % k == 0) {
					if (!l.isEmpty()) {
						list.add(l);
					}
					l = new ArrayList<Integer>();
				}
				l.add(i);
			}
			result.put(a, list);
		}
		return result;
	}
}
