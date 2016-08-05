package segment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import duration.ClusterDuration;
import parameters.FileAddresses;
import parameters.Symbols;
import activity.ActivityUtil;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import sensor.SensorUtil;

public class Segmenter {

	private static double SEMANTIC_THRESHOLD = 0.5;

	private double[][] semanticSim;

	private SensorUtil my_sensors;

	// private List<SensorEvent> my_sensor_events;

	public List<List<SensorEvent>> my_seg_sensor_events;

	public Map<String, List<DescriptiveStatistics>> my_seq_dur_clusters;

	public Segmenter(final SensorUtil the_sensors) {
		my_sensors = the_sensors;
		// getSemanticSimilarity();
	}

	// private void getSemanticSimilarity() {
	// System.out.println("sensor size: " + my_sensors.getSensors().size());
	// semanticSim = new double[my_sensors.getSensors().size()][my_sensors
	// .getSensors().size()];
	// for (int i = 0; i < my_sensors.getSensors().size(); i++) {
	// for (int j = i; j < my_sensors.getSensors().size(); j++) {
	// semanticSim[i][j] = my_sensors.similarity(
	// my_sensors.findSensor(i), my_sensors.findSensor(j));
	// semanticSim[j][i] = semanticSim[i][j];
	// }
	// }
	// // Print.print2DArray(semanticSim);
	// }

	public void segment(final List<ActivitySensorAssociation> the_events) throws IOException {

		my_seg_sensor_events = new ArrayList<List<SensorEvent>>();
		for (ActivitySensorAssociation asa : the_events) {
			my_seg_sensor_events.add(asa.getSensorEvents());
		}
		// for (SensorEvent se : the_events) {
		// boolean found = false;
		// if (!my_seg_sensor_events.isEmpty()) {
		// for (int i = 0; i < my_seg_sensor_events.size(); i++) {
		// if (semanticSim[se.getSensorId()][my_seg_sensor_events
		// .get(i).get(0).getSensorId()] >= SEMANTIC_THRESHOLD) {
		// my_seg_sensor_events.get(i).add(se);
		// found = true;
		// break;
		// }
		// }
		// }
		// if (!found) {
		// List<SensorEvent> l = new ArrayList<SensorEvent>();
		// l.add(se);
		// my_seg_sensor_events.add(l);
		// }
		// }
		// {
		// FileOutputStream fos = new FileOutputStream(FileAddresses.SE_SEG);
		// ObjectOutputStream oos = new ObjectOutputStream(fos);
		// oos.writeObject(my_seg_sensor_events);
		// oos.close();
		// fos.close();
		// }
	}

	public void retrieveSeq(final String output_addr) throws IOException {
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

		{
			FileOutputStream fos = new FileOutputStream(output_addr);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(my_seq_dur_clusters);
			oos.close();
			fos.close();
		}
	}

	// map the string and durations into indices
	public void map(final String output_addr) throws IOException {
		List<String> mapping = new ArrayList<String>();
		for (String s : my_seq_dur_clusters.keySet()) {
			for (int i = 0; i < my_seq_dur_clusters.get(s).size(); i++) {
				mapping.add(s + Symbols.DURATION_SEPARATOR + i);
			}
		}
		{
			FileOutputStream fos = new FileOutputStream(output_addr);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mapping);
			oos.close();
			fos.close();
		}
	}

}
