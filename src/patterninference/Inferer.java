package patterninference;

import java.util.List;
import sensor.SensorEvent;

public interface Inferer {
	
	public double[] infer(final List<SensorEvent> sensorEvents);

}
