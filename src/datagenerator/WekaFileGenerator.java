package datagenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;

public class WekaFileGenerator {
	final private String ACT_SYMBOL = "a";
	private int my_numOfSensors;
	private Set<Integer> my_activities;

	public WekaFileGenerator(final int the_numOfSensors, final Set<Integer> the_activities) {
		my_activities = the_activities;
		my_numOfSensors = the_numOfSensors;
	}

	public void generate(final String output_file, final List<ActivitySensorAssociation> the_data) throws IOException {
		FileWriter fw = new FileWriter(output_file);
		header(fw);
		data(fw, the_data);
	}

	private void data(FileWriter fw, final List<ActivitySensorAssociation> the_data) throws IOException {
		for (ActivitySensorAssociation asa : the_data) {
			writeArray(fw, retrieveSensorData(asa.getSensorEvents()));
			fw.write(ACT_SYMBOL + asa.getActEvent().getSensorId() + "\n");
		}
	}

	private double[] retrieveSensorData(List<SensorEvent> the_sensorData) {
		double[] result = new double[my_numOfSensors];
		for (SensorEvent se : the_sensorData) {
			result[se.getSensorId()] += 1;
		}
		return result;
	}

	private void writeArray(FileWriter fw, final double[] d) throws IOException {
		for (int i = 0; i < d.length; i++) {
			fw.write(d[i] + ",");
		}
	}

	private void header(FileWriter fw) throws IOException {
		fw.write("@RELATION data\n\n");
		for (int i = 0; i < my_numOfSensors; i++) {
			fw.write("@ATTRIBUTE s" + i + " NUMERIC\n");
		}
		fw.write("ATTRIBUTE class {");
		String act = "";
		for (int i : my_activities) {
			act += (ACT_SYMBOL + i + ",");
			// fw.write(ACT_SYMBOL + i + "}\n\n");
		}
		act = act.substring(0, act.length() - 1);
		act += "}\n\n";
		fw.write(act);
	}
}
