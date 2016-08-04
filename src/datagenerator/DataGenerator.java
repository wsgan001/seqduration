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
	final private List<ActivitySensorAssociation> my_input;
	/**
	 * the activities of interest
	 */
	private Set<Integer> my_activities;

	final private static int k = 10;

	private String my_addr;

	public DataGenerator(final List<ActivitySensorAssociation> the_data, final Set<Integer> the_activities,
			final String the_addr) {
		my_input = the_data;
		my_activities = the_activities;
		my_addr = the_addr;
	}

	/**
	 * separate the data into 10 folds
	 * 
	 * @throws IOException
	 */
	public void separate() throws IOException {
		Map<Integer, List<List<Integer>>> folds = separateIntoKFolders(getAct2Indices());
		List<List<ActivitySensorAssociation>> result = new ArrayList<List<ActivitySensorAssociation>>();
		for (int i = 0; i < k; i++) {
			result.add(new ArrayList<ActivitySensorAssociation>());
		}
		for (int act : folds.keySet()) {
			System.out.println("act " + act + " has : " + folds.get(act).size());
			for (int i = 0; i < folds.get(act).size(); i++) {
				for (int index : folds.get(act).get(i)) {
					result.get(i).add(my_input.get(index));
				}
			}
		}
		write(result);
	}

	private void write(List<List<ActivitySensorAssociation>> the_data) throws IOException {
		for (int i = 0; i < the_data.size(); i++) {
//			System.out.println("fold " + i);
//			System.out.println("first " + the_data.get(i).get(0).toString());
//			System.out.println("last " + the_data.get(i).get(the_data.get(i).size() - 1).toString());
			FileOutputStream fos = new FileOutputStream(my_addr + i);
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
			if (result.containsKey(my_input.get(i).getActEvent().getSensorId()))
				result.get(my_input.get(i).getActEvent().getSensorId()).add(i);
		}
//		System.out.println("map of act to indices: " + result);
		for (int act : result.keySet()) {
			System.out.println("act " + act + ": " + result.get(act).size() + " from: " + result.get(act).get(0) + " - "
					+ result.get(act).get(result.get(act).size() - 1));
		}
		return result;
	}

	private Map<Integer, List<List<Integer>>> separateIntoKFolders(Map<Integer, List<Integer>> act2Indices) {
		Map<Integer, List<List<Integer>>> result = new HashMap<Integer, List<List<Integer>>>();
		for (int i : my_activities) {
			List<List<Integer>> list = new ArrayList<List<Integer>>();
			for (int ki = 0; ki < k; ki++)
				list.add(new ArrayList<Integer>());
			result.put(i, list);
		}
		for (int a : act2Indices.keySet()) {
			List<Integer> set = act2Indices.get(a);
			Collections.shuffle(set);
//			final int size = Math.max((int) (Math.ceil(set.size() / k)), 1);
			for (int i = 0; i < set.size(); i++) {
				result.get(a).get((int)(i%k)).add(i);
			}
//			System.out.println("act a:" + a + " get separated indices: " + result.get(a).get(0).size());
		}
		return result;
	}
}
