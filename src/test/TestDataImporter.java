package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.joda.time.DateTime;

import sensor.SensorEvent;
import sensor.SensorUtil;
import source.washington.WashingtonInfo;
import activity.Activity;
import activity.ActivityUtil;
import concept.Concept;
import concept.ConceptUtil;
import filereader.washington.FileReaderWS;

public class TestDataImporter {
	final private static String ADDR = "../Datasets/";

	public ConceptUtil my_locations;
	public SensorUtil my_sensors;
	public ActivityUtil my_activities;
	public List<SensorEvent> my_sensor_events;
	public List<SensorEvent> my_act_events;

	public TestDataImporter() throws ClassNotFoundException, IOException {
		initialise();
	}

	public void testOverlappingActivities() {
		for (int i = 0; i < my_act_events.size() - 1; i++) {
			if (my_act_events.get(i).getEndTime() > my_act_events.get(i + 1)
					.getStartTime()) {
				Activity current = my_activities.findActivity(my_act_events
						.get(i).getSensorId());
				Activity next = my_activities.findActivity(my_act_events.get(
						i + 1).getSensorId());
				System.out.println(i
						+ ": "
						+ current.getActName()
						+ ": "
						+ new DateTime(my_act_events.get(i).getStartTime())
								.toString());
				System.out.println((i + 1)
						+ ": "
						+ next.getActName()
						+ ": "
						+ new DateTime(my_act_events.get(i + 1).getStartTime())
								.toString());
				Concept cl = my_locations.findConcept(current.getLocation()
						.get(0));
				Concept nl = my_locations
						.findConcept(next.getLocation().get(0));
				if (cl.getId() == nl.getId()
						|| my_locations.isSuper(cl, nl)
						|| my_locations.isSuper(nl, cl)
						|| my_locations.shareCommonParent(cl.getId(),
								nl.getId())) {
					System.out
							.println("SAME: " + "current location: "
									+ cl.getName() + "\tnext location: "
									+ nl.getName());
				}
			}
		}
	}

	public void initialise() throws IOException, ClassNotFoundException {
		{
			FileInputStream fis = new FileInputStream(ADDR
					+ WashingtonInfo.META_DATA);
			ObjectInputStream ois = new ObjectInputStream(fis);
			my_locations = (ConceptUtil) ois.readObject();
			my_sensors = (SensorUtil) ois.readObject();
			my_activities = (ActivityUtil) ois.readObject();
			ois.close();
			fis.close();
		}
		{
			FileReaderWS fr = new FileReaderWS(my_sensors, my_activities);
			my_sensor_events = fr.unserialiseEachEvent(ADDR
					+ WashingtonInfo.SERIALISE_SENSOR_EVENTS);
			// System.out.println("sensor event size: "+my_sensor_events.size());
			// System.out.println("first: " + my_sensor_events.get(0).print());
			// System.out
			// .println("last: "
			// + my_sensor_events.get(my_sensor_events.size() - 1)
			// .print());
			// System.out.println("middle: "
			// + my_sensor_events.get((int) my_sensor_events.size() / 2)
			// .print());
			my_act_events = fr.unserialise(ADDR
					+ WashingtonInfo.SERIALISE_DAIRY_EVENTS);
			// System.out.println("act event size: "+my_act_events.size());
			// System.out.println("first: " + my_act_events.get(0).print());
			// System.out.println("last: "
			// + my_act_events.get(my_act_events.size() - 1).print());
			// System.out.println("middle: "
			// + my_act_events.get((int) my_act_events.size() / 2)
			// .print());
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		TestDataImporter tdi = new TestDataImporter();
		tdi.testOverlappingActivities();
	}
}
