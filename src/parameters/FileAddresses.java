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
	public static String RAW_DB = "data/mined_pattern_raw/act_";

	public static String SPLIT_DB = "data/mined_pattern_split/act_";

	public static String PATTERN_AFFIX = ".out";

	public static String PATTERN_TOPK = ".topk";

	public static String RAW_FOLD = "data/fold_raw/f_";

	public static String SPLIT_FOLD = "data/fold_split/f_";

	public static String RAW_TRAIN = "data/train_test_raw/train_";

	public static String RAW_TEST = "data/train_test_raw/test_";

	public static String SPLIT_TRAIN = "data/train_test_split/train_";

	public static String SPLIT_TEST = "data/train_test_split/test_";

	public static String RAW_MAP_PATTERN = "data/mapped_seq_raw/map_seq_durations";

	public static String SPLIT_MAP_PATTERN = "data/mapped_seq_split/map_seq_durations";

	public static String RAW_MAP_INDICES = "data/mapped_seq_raw/map_seq2Indices";

	public static String SPLIT_MAP_INDICES = "data/mapped_seq_split/map_seq2Indices";
	
	public static String RAW_MINED_PATTERN = "data/mined_pattern_raw/act_";

	public static String SPLIT_MINED_PATTERN = "data/mined_pattern_split/act_";
	
	// file addresses for train and test data to do sequential mining
	public static String RAW_TRAIN_ASA = "data/raw_train/asa/train_";
	
	public static String RAW_TRAIN_MAP_SQ = "data/raw_train/map/seq_";
	
	public static String RAW_TRAIN_MAP_INDICES = "data/raw_train/map/indices_";
	
	public static String RAW_TRAIN_DB = "data/raw_train/db/act_";
	
	public static String RAW_TRAIN_PATTERN = "data/raw_train/mipatterns/act_";
	
	public static String RAW_TRAIN_PATTERN_SCORE = "data/raw_train/patternscores/act_";
}
