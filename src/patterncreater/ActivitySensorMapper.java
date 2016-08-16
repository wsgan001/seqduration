package patterncreater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import activity.ActivityUtil;
import segmentation.ActivitySensorAssociation;
import sensor.SensorEvent;
import sensor.SensorUtil;

public class ActivitySensorMapper {

	private static int MAX_MOVE = 10;

	private ActivityUtil my_activities;

	private SensorUtil my_sensors;

	public ActivitySensorMapper(final ActivityUtil the_activities,
			final SensorUtil the_sensors) {
		my_sensors = the_sensors;
		my_activities = the_activities;
	}

	private boolean add(List<SensorEvent> one_seg, int indices, SensorEvent ae,
			ActivitySensorAssociation asa) {
		boolean addSensor = false;
		// System.out.println("found seg id: "+i);
		Set<Integer> searched = new HashSet<Integer>();
		while (indices >= 0 && indices < one_seg.size()) {
			// System.out.println("start searching: "+indices[i]);
			if (searched.contains(indices)) {
				// (!addSensor && searched.size() >= MAX_MOVE) ||
				break;
			} else if (one_seg.get(indices).getStartTime() >= ae.getStartTime()
					&& one_seg.get(indices).getStartTime() <= ae.getEndTime()) {
				// System.out.println("found the sensor event: "
				// + indices[i]);
				asa.addSensorEvents(one_seg.get(indices));
				searched.add(indices);
				indices++;
				addSensor = true;
			} else if (one_seg.get(indices).getStartTime() < ae.getStartTime()) {
				searched.add(indices);
				indices++;
			} else if (one_seg.get(indices).getStartTime() > ae.getEndTime()) {
				searched.add(indices);
				indices--;
			}
		}
		return addSensor;
	}

	public List<ActivitySensorAssociation> map(
			final List<List<SensorEvent>> seg_sensor_events,
			final List<SensorEvent> the_act_events) {
		List<ActivitySensorAssociation> result = new ArrayList<ActivitySensorAssociation>();
		int[] indices = new int[seg_sensor_events.size()];
		int index = 0;
		for (SensorEvent ae : the_act_events) {
			System.out.println(index++);
			ActivitySensorAssociation asa = new ActivitySensorAssociation(ae);
			int found = findCompatibleSeg(ae, seg_sensor_events);
			System.out.println("first find by compatible: "+found);
			if (found < 0) {
				found = findCompatibleBySimilarity(ae, seg_sensor_events, found);
				System.out.println("then find by similarity: "+found);
			}
			boolean addSensor = add(seg_sensor_events.get(found),
					indices[found], ae, asa);
			if (!addSensor) {
				found = findCompatibleBySimilarity(ae, seg_sensor_events, found);
				System.out.println("failed adding event and find by similarity: "+found);
				addSensor = add(seg_sensor_events.get(found), indices[found], ae, asa);
			}
			// System.out.println("finished: ");
			// asa.print();
			result.add(asa);
			if (found < 0)
				System.out
						.println("Not found matched sensor for the activity event: "
								+ ae.print());
			if (!addSensor && found >= 0) {
				System.out.println("not found matched event: " + ae.print());
				System.out.println("segment id: " + found);
				System.out.println("stopped sensor event: "
						+ seg_sensor_events.get(found).get(indices[found])
								.print());
			}
		}
		return result;
	}

	private int findCompatibleBySimilarity(SensorEvent act_event,
			List<List<SensorEvent>> segs, int the_previous_seg_id) {
		int found = -1;
		final List<Integer> act_locations = my_activities.findActivity(
				act_event.getSensorId()).getLocation();
		double max_sim = 0;
		for (int al : act_locations) {
			for (int i = 0; i < segs.size(); i++) {
				if (i != found) {
					int sl = my_sensors.findSensor(
							segs.get(i).get(0).getSensorId()).getLocation();
					if (my_sensors.getLocation().similarity(al, sl) > max_sim) {
						max_sim = my_sensors.getLocation().similarity(al, sl);
						found = i;
						break;
					}
				}
			}
		}
		return found;
	}

	private int findCompatibleSeg(SensorEvent act_event,
			List<List<SensorEvent>> segs) {
		int found = -1;
		final List<Integer> act_locations = my_activities.findActivity(
				act_event.getSensorId()).getLocation();
		for (int al : act_locations) {
			for (int i = 0; i < segs.size(); i++) {
				int sl = my_sensors
						.findSensor(segs.get(i).get(0).getSensorId())
						.getLocation();
				if (al == sl
						|| my_sensors.getLocation().shareCommonParent(al, sl)
						|| my_sensors.getLocation().isSuper(
								my_sensors.getLocation().findConcept(al),
								my_sensors.getLocation().findConcept(sl))
						|| my_sensors.getLocation().isSuper(
								my_sensors.getLocation().findConcept(sl),
								my_sensors.getLocation().findConcept(al))) {
					found = i;
					break;
				}
			}
		}
		return found;
	}

	private boolean compatible(SensorEvent act_event, SensorEvent sensor_event) {
		final List<Integer> act_locations = my_activities.findActivity(
				act_event.getSensorId()).getLocation();
		final int sensor_location = my_sensors.findSensor(
				sensor_event.getSensorId()).getLocation();
		System.out.println("act locations: "
				+ my_sensors.getLocation().findConcept(act_locations.get(0))
						.getName()
				+ "\tsensor locations: "
				+ my_sensors.getLocation().findConcept(sensor_location)
						.getName());
		boolean match = false;
		for (int al : act_locations) {
			if (al == sensor_location
					|| my_sensors.getLocation().isSuper(
							my_sensors.getLocation().findConcept(al),
							my_sensors.getLocation().findConcept(
									sensor_location))
					|| my_sensors.getLocation().isSuper(
							my_sensors.getLocation().findConcept(
									sensor_location),
							my_sensors.getLocation().findConcept(al))
					|| my_sensors.getLocation().shareCommonParent(al,
							sensor_location)) {
				// || my_sensors.getLocation().similarity(sensor_location, al) >
				// 0.5) {
				// System.out.println("found");
				match = true;
				break;
			}
		}
		return match;
	}
}
