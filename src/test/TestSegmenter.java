package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import parameters.FileAddresses;
import segment.Segmenter;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import sensor.SensorUtil;
import source.washington.WashingtonInfo;
import activity.ActivityUtil;
import concept.ConceptUtil;
import filereader.washington.FileReaderWS;

public class TestSegmenter {

	private TestDataImporter my_data;
	
	public TestSegmenter() throws ClassNotFoundException, IOException {
		my_data = new TestDataImporter();
	}

	public void segment() throws IOException, ClassNotFoundException {
		Segmenter seg = new Segmenter(my_data.my_sensors);
		FileInputStream fis = new FileInputStream(FileAddresses.SE_SEG);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<ActivitySensorAssociation> list = (List<ActivitySensorAssociation>)ois.readObject();
		ois.close();
		fis.close();
		seg.segment(list);
		// List<List<SensorEvent>> segs = seg.my_seg_sensor_events;
		seg.retrieveSeq();
		seg.map();
		// System.out.println("first: " + segs.get(0).get(0).print());
		// System.out.println("last: " + segs.get(segs.size() -
		// 1).get(segs.get(segs.size()-1).size()-1).print());
		// System.out.println("middle: "
		// + segs.get((int) segs.size() / 2).get(segs.get((int) segs.size() /
		// 2).size()-1).print());
	}

	public void testSeq() throws IOException, ClassNotFoundException {
		{
			FileInputStream fis = new FileInputStream(
					FileAddresses.SEQUENCE_CLUSTER_DURATION);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Map<String, List<DescriptiveStatistics>> cluster = (Map<String, List<DescriptiveStatistics>>) ois
					.readObject();
			ois.close();
			fis.close();
			int index = 0;
			System.out.println("number of unique 2-length sequences: "
					+ cluster.size());
			for (String s : cluster.keySet()) {
				System.out.println(s);
				index += cluster.get(s).size();
			}
			System.out.println("total: " + index);
		}
		{
			FileInputStream fis = new FileInputStream(
					FileAddresses.MAP_SEQ_DURATION_INDEX);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<String> l = (List<String>) ois.readObject();
			ois.close();
			fis.close();
			System.out.println("mapping total: " + l.size());
			System.out.println(l.get(0));
			System.out.println(l.get(l.size() - 1));
			System.out.println(l.get(l.size() / 2));
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		TestSegmenter ts = new TestSegmenter();
//		 ts.segment();
		ts.testSeq();
	}
}
