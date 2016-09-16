package patterncreater;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;

public class PatternMapper {
	final private static int NUM_OF_ACT = 25;
	private List<String> my_seq2index;

	private Map<String, List<DescriptiveStatistics>> my_seq2Clusters;

	public PatternMapper(final String map2SeqDuration, final String map2Indices) throws IOException, ClassNotFoundException {
		{
			FileInputStream fis = new FileInputStream(map2SeqDuration);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_seq2Clusters = (Map<String, List<DescriptiveStatistics>>) ois
					.readObject();
			ois.close();
			fis.close();
		}
		{
			FileInputStream fis = new FileInputStream(map2Indices);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_seq2index = (List<String>) ois.readObject();
			ois.close();
			fis.close();
		}
	}

	private FileWriter[] initialiseFileWriter(final String addr) throws IOException {
		FileWriter[] fw = new FileWriter[NUM_OF_ACT];
		for (int i = 0; i < NUM_OF_ACT; i++) {
			fw[i] = new FileWriter(addr + "_"+i);
		}
		return fw;
	}


	private String getList(final List<SensorEvent> list) {
		String result = "";
		for (int i = 1; i < list.size(); i++) {
			String pair = list.get(i - 1).getSensorId() + Symbols.SEQ_SEPARATOR
					+ list.get(i).getSensorId();
			final double duration = (list.get(i).getStartTime() - list.get(
					i - 1).getStartTime()) / 1000;
			final int cID = PatternUtil.map2cluster(my_seq2Clusters.get(pair), duration);
			result += findIndex(pair + Symbols.DURATION_SEPARATOR + cID)
					+ Symbols.PATTERN_SEPARATOR;
		}
		return result + Symbols.PATTERN_END;
	}

	private int findIndex(String s) {
		int id = -1;
		for(int i=0; i< my_seq2index.size(); i++) {
			if (my_seq2index.get(i).equals(s)) {
				id = i;
				break;
			}
		}
		if (id==-1) {
			System.out.println(s);
		}
		return id;
	}
	public void write(List<ActivitySensorAssociation> input, final String addr) throws IOException {
		FileWriter[] fw = initialiseFileWriter(addr);
		for (ActivitySensorAssociation asa : input) {
			fw[asa.getActEvent().getSensorId()].write(getList(asa
					.getSensorEvents()));
		}
		close(fw);
	}
	
	private void close(FileWriter[] fw) throws IOException {
		for(int i=0; i<fw.length; i++) {
			fw[i].close();
		}
	}
}
