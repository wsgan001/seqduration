package duration;

import java.util.Collection;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.Model;
import org.apache.mahout.common.parameters.Parameter;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class Duration implements Clusterable {

	private double[] my_data;
	
	public Duration(final double d) {
		my_data = new double[]{d};
	}
	
	@Override
	public double[] getPoint() {
		return my_data;
	}



}
