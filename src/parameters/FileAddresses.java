package parameters;

public interface FileAddresses {
	public static String SE_SEG = "serialise/sensor-event-segments";
	// store (s0-s1: all duration) Map<Integer, Map<String,
	// DescriptiveStatistics>>
//	public static String SEQUENCE_DURATION = "serialise/seq-duration";
	// store (s0-s1, clusters of durations)
	public static String SEQUENCE_CLUSTER_DURATION = "serialise/seq-cluster-duration";
	// store the mapping between (s0-s1, cluster duration) and indices
	public static String MAP_SEQ_DURATION_INDEX = "serialise/seq-duration-index-mapping";
	// store the map<activity id, list<index of seq and duration patterns>
	public static String MAP_SEQ_ID = "serialise/seq_id";
	// store the mined pattern
	public static String PATTERN = "pattern/act_";
	
	public static String PATTERN_AFFIX = ".out";
	
	public static String PATTERN_TOPK= ".topk";
}
