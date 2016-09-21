package v2.patternminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.Symbols;
import test.Print;
import v2.data.DataUtil;
import v2.data.Parameters;

public class PatternRecoverer {

	private Map<String, List<DescriptiveStatistics>> sq2_duration;
	private List<String> indices;

	public PatternRecoverer(final int[] acts, final String appendix, final int trainId)
			throws IOException, ClassNotFoundException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, acts);
		// retrieve the mapper
		FileInputStream fis = new FileInputStream(group_dir + Parameters.PATTERNS_MAPPER_DIR + appendix + trainId);
		ObjectInputStream ois = new ObjectInputStream(fis);
		sq2_duration = (Map<String, List<DescriptiveStatistics>>) ois.readObject();
		indices = (List<String>) ois.readObject();
	}

	public void start(final String group_dir, final String score_file) throws IOException, ClassNotFoundException {
		// retrieve the score
		FileInputStream fos = new FileInputStream(group_dir + Parameters.PATTERNS_SCORE_DIR + score_file);
		ObjectInputStream oos = new ObjectInputStream(fos);
		Map<String, double[]> pattern_score = (Map<String, double[]>) oos.readObject();
		oos.close();
		fos.close();
		// start change
		List<Pattern> result = new ArrayList<Pattern>();
		for (String p : pattern_score.keySet()) {
			Pattern ap = convert(p, pattern_score.get(p));
			result.add(ap);
		}
		// serialise
		{
			File f = new File(group_dir + Parameters.PATTERNS_RECOVER_DIR);
			if (!f.exists()) {
				f.mkdir();
			}
		}
		FileOutputStream fis = new FileOutputStream(group_dir + Parameters.PATTERNS_RECOVER_DIR + score_file);
		ObjectOutputStream ois = new ObjectOutputStream(fis);
		ois.writeObject(result);
		ois.close();
		fis.close();
	}

	public Pattern convert(String p, double[] scores) {
		if (!p.trim().isEmpty()) {
			final String[] ps = p.split(Symbols.PATTERN_SEPARATOR);
			Pattern ap = new Pattern();
			ap.sensorIds = new int[ps.length + 1];
			ap.durationIds = new int[ps.length];
			ap.scores = scores;
			for (int i = 0; i < ps.length; i++) {
				final int[] sensor_duration = retrieveSIdsDuration(indices.get(Integer.parseInt(ps[i])));
				if (i == 0) {
					ap.sensorIds[i] = sensor_duration[0];
				}
				ap.sensorIds[i + 1] = sensor_duration[1];
				ap.durationIds[i] = sensor_duration[2];
			}
			return ap;
		}
		return null;
	}

	private int[] retrieveSIdsDuration(final String sensor_duration) {
		String[] ids_dur = sensor_duration.split(Symbols.DURATION_SEPARATOR);
		String[] ids = ids_dur[0].split(Symbols.SEQ_SEPARATOR);
		return new int[] { Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), Integer.parseInt(ids_dur[1]) };
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		final String group_dir = DataUtil.generateGroupFileName(Parameters.GROUPS_DIR, Parameters.R1_ROOM);
		for (int i = 0; i < 1; i++) {
			// PatternRecoverer pr = new PatternRecoverer(Parameters.R1_ROOM,
			// Parameters.SOURCE, i);
			for (int a : Parameters.R1_ROOM) {
				System.out.println("act :"+a);
				// pr.start(group_dir, Parameters.SOURCE + i + "_" + a);
				// test
				FileInputStream fis = new FileInputStream(
						group_dir + Parameters.PATTERNS_RECOVER_DIR + Parameters.SOURCE + i + "_" + a);
				ObjectInputStream ois = new ObjectInputStream(fis);
				List<Pattern> list = (List<Pattern>) ois.readObject();
				ois.close();
				fis.close();
				for(Pattern p: list) {
					System.out.print("\nsensor ids: " );
					Print.printArray(p.sensorIds);
					System.out.print("\nduration ids: " );
					Print.printArray(p.durationIds);
					System.out.print("\nscores: " );
					Print.printArray(p.scores);
				}
			}
		}

	}
}
