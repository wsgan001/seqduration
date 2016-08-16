package patterncreater;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import parameters.FileAddresses;
import concept.ConceptUtil;
import segmentation.ActivitySensorAssociation;
import sensor.Sensor;
import sensor.SensorEvent;
import sensor.SensorUtil;
import activity.Activity;
import activity.ActivityUtil;

/**
 * activity event map to sensor event
 * 
 * @author juan
 *
 */
public class ActivitySensorMapper2 {

	private ActivityUtil my_activities;

	private SensorUtil my_sensors;

	public ActivitySensorMapper2(final ActivityUtil the_activities,
			final SensorUtil the_sensors) {
		my_sensors = the_sensors;
		my_activities = the_activities;
	}

	private boolean match(SensorEvent actEvent, SensorEvent sEvent) {
		boolean matched = false;
		int al = my_activities.findActivity(actEvent.getSensorId())
				.getLocation().get(0);
		int sl = my_sensors.findSensor(sEvent.getSensorId()).getLocation();
		ConceptUtil locs = my_sensors.getLocation();
		if (al == sl
				|| locs.isSuper(locs.findConcept(al), locs.findConcept(sl))) {
			matched = true;
		}
		return matched;
	}

	private int add(ActivitySensorAssociation asa, int current_index,
			List<SensorEvent> the_sensor_events) {
		List<Integer> searched = new ArrayList<Integer>();
		boolean added = false;
		final long start = asa.getActEvent().getStartTime();
		final long end = asa.getActEvent().getEndTime();
//		System.out.println(start + " - " + end);
		while (current_index >= 0 && current_index < the_sensor_events.size()) {
//			System.out.println("search sensor event: "+current_index);
			if (searched.contains(current_index)) {
//				System.out.println("Break");
				break;
			} else if (the_sensor_events.get(current_index).getStartTime() >= start
					&& the_sensor_events.get(current_index).getStartTime() <= end) {
				if (match(asa.getActEvent(),
						the_sensor_events.get(current_index))) {
//					System.out.println("found event at: " + current_index);
					asa.addSensorEvents(the_sensor_events.get(current_index));
					added = true;
				}
				searched.add(current_index);
				current_index++;
			} else if (the_sensor_events.get(current_index).getStartTime() < start) {
//				System.out.println("Move forward: " + current_index + ": "
//						+ the_sensor_events.get(current_index).getStartTime());
				searched.add(current_index);
				current_index++;
			} else if (the_sensor_events.get(current_index).getStartTime() > end) {
//				System.out.println("Move backward: " + current_index + ": "
//						+ the_sensor_events.get(current_index).getStartTime());
				if (added) break;
				searched.add(current_index);
				current_index--;
//			} else {
//				System.out.println(current_index + ": "
//						+ the_sensor_events.get(current_index).getStartTime());
			}
		}
//		System.out.println("stop at; "+current_index);
		if (!added && !searched.isEmpty()) {
			current_index = searched.get(0);
		}
//		System.out.println("recover: "+current_index);
		return current_index;
	}

	public List<ActivitySensorAssociation> map(
			final List<SensorEvent> sensorEvents,
			final List<SensorEvent> actEvents) throws IOException {
		List<ActivitySensorAssociation> result = new ArrayList<ActivitySensorAssociation>();
		// index of the sensor events
		int index = 0;
//		int c = 0;
		for (SensorEvent ae : actEvents) {
//			System.out.println("act event:"+c++); 
//					+ ae.print());
			ActivitySensorAssociation asa = new ActivitySensorAssociation(ae);
			int nindex = add(asa, index, sensorEvents);
			if (nindex > index) {
				result.add(asa);
//				 } else {
//				 System.out.println("NOT FOUND SENSOR EVENT: " + my_activities.findActivity(ae.getSensorId()).getActName()+ "-"+ ae.print());
			}
			index = nindex;
		}
		{
			FileOutputStream fos = new FileOutputStream(FileAddresses.SE_SEG);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(result);
			oos.close();
			fos.close();
		}
		return result;
	}

}
