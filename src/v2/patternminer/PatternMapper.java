package v2.patternminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import parameters.Symbols;
import patterncreater.PatternUtil;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import v2.data.DataUtil;
import v2.data.Parameters;

/**
 * map sensor events to indices of <s1, s2, duration cluster id>
 * 
 * @author juan
 *
 */
public class PatternMapper {
	private int[] my_acts;

	private List<String> my_seq2index;

	private Map<String, List<DescriptiveStatistics>> my_seq2Clusters;

	private Map<Integer, Integer> my_actID_index;

	public PatternMapper(final String mapper_file, final int[] acts) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(mapper_file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		my_seq2Clusters = (Map<String, List<DescriptiveStatistics>>) ois.readObject();
		my_seq2index = (List<String>) ois.readObject();
		ois.close();
		fis.close();
		my_acts = acts;
		initialiseIndex();
	}

	private void initialiseIndex() {
		my_actID_index = new HashMap<Integer, Integer>();
		int index = 0;
		for (int i : my_acts) {
			my_actID_index.put(i, index);
			index++;
		}
	}

	private FileWriter[] initialiseFileWriter(final int train_index, final String appendix, final String trainTestDir)
			throws IOException {
		// initialise pattern dir
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, my_acts);
		{
			File f = new File(group_dir + Parameters.PATTERNS_SQUENTIAL_DIR);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		{
			File f = new File(group_dir + Parameters.PATTERNS_SQUENTIAL_PATTERNS_DIR);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		{
			File f = new File(group_dir + Parameters.PATTERNS_SCORE_DIR);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		{
			File f = new File(group_dir + trainTestDir);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		FileWriter[] fw = new FileWriter[my_acts.length];
		for (int i = 0; i < my_acts.length; i++) {
			fw[i] = new FileWriter(group_dir + trainTestDir + appendix + train_index + "_" + my_acts[i]);
		}
		return fw;
	}

	private String getList(final List<SensorEvent> list) {
		String result = "";
		for (int i = 1; i < list.size(); i++) {
			String pair = list.get(i - 1).getSensorId() + Symbols.SEQ_SEPARATOR + list.get(i).getSensorId();
			final double duration = (list.get(i).getStartTime() - list.get(i - 1).getStartTime()) / 1000;
			int cID = -1;
			if (my_seq2Clusters.containsKey(pair)) {
				cID = PatternUtil.map2cluster(my_seq2Clusters.get(pair), duration);	
				result += findIndex(pair + Symbols.DURATION_SEPARATOR + cID) + Symbols.PATTERN_SEPARATOR;
			} else {
				System.out.println(pair + " not exist");
				
			}
			
		}
		return result + Symbols.PATTERN_END;
	}

	private int findIndex(String s) {
		int id = -1;
		for (int i = 0; i < my_seq2index.size(); i++) {
			if (my_seq2index.get(i).equals(s)) {
				id = i;
				break;
			}
		}
		if (id == -1) {
			System.out.println(s);
		}
		return id;
	}

	public void write(List<ActivitySensorAssociation> input, final int train_index, final String appendix,
			final String trainOrTestDir) throws IOException {
		FileWriter[] fw = initialiseFileWriter(train_index, appendix, trainOrTestDir);
		for (ActivitySensorAssociation asa : input) {
			fw[my_actID_index.get(asa.getActEvent().getSensorId())].write(getList(asa.getSensorEvents()));
		}
		close(fw);
	}

	private void close(FileWriter[] fw) throws IOException {
		for (int i = 0; i < fw.length; i++) {
			fw[i].close();
		}
	}

	public static void run(final int[] acts, final String appendix) throws IOException, ClassNotFoundException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		FileInputStream fis = new FileInputStream(group_dir + "/" + appendix);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<List<ActivitySensorAssociation>> train = (List<List<ActivitySensorAssociation>>) ois.readObject();
		ois.close();
		fis.close();
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			// retrieve 2-length pattern data
			PatternMapper pm = new PatternMapper(group_dir + Parameters.PATTERNS_MAPPER_DIR + appendix + i, acts);
			pm.write(train.get(i), i, appendix, Parameters.PATTERNS_SQUENTIAL_CONVERTED_FILES_DIR);
		}
	}

	public static void convertTestData(final int[] acts, final String appendix) throws IOException, ClassNotFoundException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		FileInputStream fis = new FileInputStream(group_dir + "/" + appendix);
		ObjectInputStream ois = new ObjectInputStream(fis);
		ois.readObject();
		List<List<ActivitySensorAssociation>> test = (List<List<ActivitySensorAssociation>>) ois.readObject();
		ois.close();
		fis.close();
		for (int i = 0; i < Parameters.NUM_OF_FOLDERS; i++) {
			// retrieve 2-length pattern data
			PatternMapper pm = new PatternMapper(group_dir + Parameters.PATTERNS_MAPPER_DIR + appendix + i, acts);
			pm.write(test.get(i), i, appendix, Parameters.PATTERNS_SQUENTIAL_CONVERTED_TEST_DIR);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
//		run(Parameters.R1_ROOM, Parameters.SOURCE);
		convertTestData(Parameters.R1_ROOM, Parameters.SOURCE);
	}
}
