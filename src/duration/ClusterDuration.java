package duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ClusterDuration {

	// private KMeansPlusPlusClusterer my_cluster;

	private List<Duration> my_data;

	final private static int ITERATIONS = 200;

	final private int MIN_K = 2;
	final private int MAX_K;

	private double[] my_F;
	private double my_mean;

	public ClusterDuration(final DescriptiveStatistics ds) {

		getData(ds);
		// System.out.println("data size: " + ds.getN() + "=="
		// + ds.getValues().length + "==" + my_data.size());
		// my_cluster = new KMeansPlusPlusClusterer(2, ITERATIONS);
		my_mean = ds.getMean();
		MAX_K = Math.max(MIN_K, (int) Math.sqrt(ds.getN()));
//		System.out.println("k min - max:" + MIN_K + " - " + MAX_K);
	}

	private void getData(DescriptiveStatistics ds) {
		my_data = new ArrayList<Duration>();
		for (double d : ds.getValues()) {
			my_data.add(new Duration(d));
		}
	}

	/**
	 * between group variance
	 * 
	 * @param the_clusters
	 * @return
	 */
	private double getExplainedVariance(
			List<CentroidCluster<Duration>> the_clusters) {
		double result = 0;
		for (CentroidCluster<Duration> c : the_clusters) {
			result += c.getPoints().size()
					* Math.pow(c.getCenter().getPoint()[0] - my_mean, 2);
		}
		return result / (the_clusters.size() - 1);
	}

	private double getUnexplainedVariance(
			List<CentroidCluster<Duration>> the_clusters) {
		double result = 0;
		for (CentroidCluster<Duration> cc : the_clusters) {
			for (Duration p : cc.getPoints()) {
				result += Math.pow(p.getPoint()[0]
						- cc.getCenter().getPoint()[0], 2);
			}
		}
		return result / (my_data.size() - the_clusters.size());
	}

	public double getF(List<CentroidCluster<Duration>> the_clusters) {
		return getExplainedVariance(the_clusters)
				/ getUnexplainedVariance(the_clusters);
	}

	private int findK() {
		double diff = 0;
		int the_k = -1;
		for (int i = 1; i < my_F.length; i++) {
			if (my_F[i] - my_F[i - 1] >= diff) {
				diff = my_F[i] - my_F[i - 1];
				the_k = i;
			} else {
				break;
			}
		}
		return the_k;
	}

	/**
	 * 
	 * @param all_data
	 *            passed from descriptiveStatistics.getData(); all the duration
	 *            data.
	 */
	public List<DescriptiveStatistics> run() {
		// Map<Integer, List<CentroidCluster>> map = new TreeMap<Integer,
		// List<CentroidCluster>>();
		if (my_data.size() <= 2) {
//			System.out
//					.println("No need to cluster as the samle size is too small!");
			List<DescriptiveStatistics> list = new ArrayList<DescriptiveStatistics>();
			DescriptiveStatistics ds = new DescriptiveStatistics();
			for (Duration d : my_data) {
				ds.addValue(d.getPoint()[0]);
			}
			list.add(ds);
			return list;
		} else {
			double max_diff = 0;
			double previous_F = 0;
			List<CentroidCluster<Duration>> result = null;
			for (int k = MIN_K; k <= MAX_K; k++) {
				KMeansPlusPlusClusterer<Duration> my_cluster = new KMeansPlusPlusClusterer<Duration>(
						k, ITERATIONS);
				// System.out.println(my_data.size());
				final List<CentroidCluster<Duration>> clusteres = my_cluster
						.cluster(my_data);
				// System.out.println(k + ": " + clusteres.size());
				result = clusteres;
				final double current_F = getF(clusteres);
				if (previous_F != 0) {
					if (current_F - previous_F >= max_diff) {
						max_diff = current_F - previous_F;
					} else {
						break;
					}
				}
				previous_F = current_F;
			}
			return convert2DS(result);
		}
	}

	private List<DescriptiveStatistics> convert2DS(
			List<CentroidCluster<Duration>> the_clusters) {
		List<DescriptiveStatistics> result = new ArrayList<DescriptiveStatistics>();
		for (CentroidCluster<Duration> cc : the_clusters) {
			DescriptiveStatistics ds = new DescriptiveStatistics();
			for (Duration p : cc.getPoints()) {
				ds.addValue(p.getPoint()[0]);
			}
			if (ds.getN() > 0) {
				result.add(ds);
			}
		}
		return result;
	}
}