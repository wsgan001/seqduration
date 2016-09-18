package v2.weka;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;

public class ARFFGenerator {
	final private static String ACT_SYMBOL = "a";

	private int feature_number;
	private int[] classes;
	private List<ActivitySensorAssociation> data;

	public ARFFGenerator(final int sensorNumber, final int[] activities,
			final List<ActivitySensorAssociation> the_data) {
		feature_number = sensorNumber;
		classes = activities;
		data = the_data;
	}

	public void generate(final String output_file) throws IOException {
		FileWriter fw = new FileWriter(output_file + ".arff");
		header(fw);
		fw.write("@data\n");
		data(fw);
		fw.close();
	}

	private void data(FileWriter fw) throws IOException {
		for (ActivitySensorAssociation asa : data) {
			writeArray(fw, retrieveSensorData(asa.getSensorEvents()));
			fw.write(ACT_SYMBOL + asa.getActEvent().getSensorId() + "\n");
		}
	}

	private double[] retrieveSensorData(List<SensorEvent> the_sensorData) {
		double[] result = new double[feature_number];
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
		for (int i = 0; i < feature_number; i++) {
			fw.write("@ATTRIBUTE s" + i + " NUMERIC\n");
		}
		fw.write("@ATTRIBUTE class {");
		String act = "";
		for (int i : classes) {
			act += (ACT_SYMBOL + i + ",");
			// fw.write(ACT_SYMBOL + i + "}\n\n");
		}
		act = act.substring(0, act.length() - 1);
		act += "}\n\n";
		fw.write(act);
	}

}
