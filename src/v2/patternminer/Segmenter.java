package v2.patternminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import duration.ClusterDuration;
import parameters.Symbols;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import v2.data.DataUtil;
import v2.data.Parameters;
/**
 * extract 2-length patterns
 * cluster durations
 * map <s1, s2, cluster id> to indices 
 * @author juan
 *
 */
public class Segmenter {

	public List<List<SensorEvent>> my_seg_sensor_events;

	public Map<String, List<DescriptiveStatistics>> my_seq_dur_clusters;

	private List<String> my_mappings;

	public Segmenter(final List<ActivitySensorAssociation> the_events, final String output_file) throws IOException {
		segment(the_events);
		retrieveSeq();
		map();
		FileOutputStream fos = new FileOutputStream(output_file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(my_seq_dur_clusters);
		oos.writeObject(my_mappings);
		oos.flush();
		oos.close();
		fos.close();
	}

	public void segment(final List<ActivitySensorAssociation> the_events) {

		my_seg_sensor_events = new ArrayList<List<SensorEvent>>();
		for (ActivitySensorAssociation asa : the_events) {
			my_seg_sensor_events.add(asa.getSensorEvents());
		}
	}

	public void retrieveSeq() {
		Map<String, DescriptiveStatistics> seq_durations = new TreeMap<String, DescriptiveStatistics>();
		for (List<SensorEvent> l : my_seg_sensor_events) {
			for (int i = 1; i < l.size(); i++) {
				String s = l.get(i - 1).getSensorId() + Symbols.SEQ_SEPARATOR + l.get(i).getSensorId();
				if (!seq_durations.containsKey(s)) {
					seq_durations.put(s, new DescriptiveStatistics());
				}
				seq_durations.get(s).addValue((l.get(i).getStartTime() - l.get(i - 1).getStartTime()) / 1000);
			}
		}
		my_seq_dur_clusters = new TreeMap<String, List<DescriptiveStatistics>>();
		for (String s : seq_durations.keySet()) {
			ClusterDuration cd = new ClusterDuration(seq_durations.get(s));
			my_seq_dur_clusters.put(s, cd.run());
		}

	}

	// map the string and durations into indices
	public void map() {
		my_mappings = new ArrayList<String>();
		for (String s : my_seq_dur_clusters.keySet()) {
			for (int i = 0; i < my_seq_dur_clusters.get(s).size(); i++) {
				my_mappings.add(s + Symbols.DURATION_SEPARATOR + i);
			}
		}
	}

	public static void run(int[] acts, final String appendix) throws IOException, ClassNotFoundException {
		final String dir_name = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			//read data
			FileInputStream fis = new FileInputStream(dir_name+"/"+appendix);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<List<ActivitySensorAssociation>> train = (List<List<ActivitySensorAssociation>>) ois.readObject();
			ois.close();
			fis.close();
			// collect train data
			{
				File f = new File(dir_name+Parameters.PATTERNS_MAPPER_DIR);
				if (!f.exists()) {
					f.mkdir();
				}
			}
			new Segmenter(train.get(i), dir_name+Parameters.PATTERNS_MAPPER_DIR+appendix+i);
		}
	}
	
	public static void test(final String input_file) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(input_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ois.readObject();
		List<String> indices = (List<String>)ois.readObject();
		System.out.println(indices);
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		run(Parameters.R1_ROOM, Parameters.SOURCE);
//		test("data.v2/groups/g_0_11_15/p/m/split5");
	}
}
