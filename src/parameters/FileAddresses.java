package parameters;

public interface FileAddresses {
	public static String SE_SEG = "data/segments/sensor-event-segments";

	public static String RAW_ASSOCIATION = "data/segments/raw-association";

	public static String SPLIT_ASSOCIATION = "data/segments/split-association";
	// store (s0-s1: all duration) Map<Integer, Map<String,
	// DescriptiveStatistics>>
	// public static String SEQUENCE_DURATION = "serialise/seq-duration";
	// store (s0-s1, clusters of durations)
	public static String SEQUENCE_CLUSTER_DURATION = "data/extracted_seq/seq-cluster-duration";
	// store the mapping between (s0-s1, cluster duration) and indices
	public static String MAP_SEQ_DURATION_INDEX = "data/extracted_seq/seq-duration-index-mapping";
	// store the map<activity id, list<index of seq and duration patterns>
	// store the mined pattern
	public static String DB = "data/mined_pattern/act_";

	public static String PATTERN_AFFIX = ".out";

	public static String PATTERN_TOPK = ".topk";

	public static String RAW_FOLD = "data/fold_raw/f_";

	public static String SPLIT_FOLD = "data/fold_split/f_";

	public static String RAW_TRAIN = "data/train_test_raw/train_";

	public static String RAW_TEST = "data/train_test_raw/test_";

	public static String SPLIT_TRAIN = "data/train_test_split/train_";

	public static String SPLIT_TEST = "data/train_test_split/test_";
}
