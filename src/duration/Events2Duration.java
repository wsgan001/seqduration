package duration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;

public class Events2Duration {
	// all the sensor events
	private List<SensorEvent> my_sensor_events;
	// map<2-length sequence, duratins>
	private Map<String, DescriptiveStatistics> my_seq_durations;
	// map<2-length sequence, duration clusters>
	private Map<String, List<DescriptiveStatistics>> my_seq_durations_clusters;
	// map<2-length sequence with duration cluster id, integer>
	private Map<String, Integer> my_seq_index;

	public Events2Duration(final List<SensorEvent> the_sensorEvents) {
		my_sensor_events = the_sensorEvents;
	}

	public void getDurations() {
		my_seq_durations = new TreeMap<String, DescriptiveStatistics>();
		for (int i = 1; i < my_sensor_events.size(); i++) {
			String s = my_sensor_events.get(i - 1).getSensorId()
					+ Symbols.SEQ_SEPARATOR
					+ my_sensor_events.get(i).getSensorId();
			if (!my_seq_durations.containsKey(s)) {
				my_seq_durations.put(s, new DescriptiveStatistics());
			}
			my_seq_durations.get(s).addValue(
					(my_sensor_events.get(i).getStartTime() - my_sensor_events
							.get(i - 1).getStartTime()) / 1000);
		}
	}

	public void clusterDurations() throws IOException {
		for (String s : my_seq_durations.keySet()) {
			ClusterDuration cd = new ClusterDuration(my_seq_durations.get(s));
			my_seq_durations_clusters.put(s, cd.run());
		}
		FileOutputStream fos = new FileOutputStream(
				FileAddresses.SEQUENCE_CLUSTER_DURATION);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(my_seq_durations_clusters);
		oos.close();
		fos.close();
	}

	public void indices() {
		my_seq_index = new TreeMap<String, Integer>();
		int index = 0;
		for (String s : my_seq_durations_clusters.keySet()) {
			for (int i = 0; i < my_seq_durations_clusters.size(); i++) {
				my_seq_index.put(s + Symbols.DURATION_SEPARATOR + i, index++);
			}
		}
	}
	
	public void convertBack(boolean splitOrNot, final List<ActivitySensorAssociation> input) {
		// already split into 
		for(ActivitySensorAssociation asa: input) {
			
		}
		 
	}
}
