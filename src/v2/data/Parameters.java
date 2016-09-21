package v2.data;

public interface Parameters {

	final public String SOURCE = "source";

	final public String SPLIT = "split";

	final public String RAW_MAPPING = "data/segments/raw-association";

	final public String ACT_SPLIT_DIR = "data.v2/" + SOURCE + "/a";
	// split the association into minitues
	final public String ACT_SPLIT_MIN_DIR = "data.v2/" + SPLIT + "/a";
	// number of folders
	final public int NUM_OF_FOLDERS = 10;
	// grouping of activities
	final public String GROUPS_DIR = "data.v2/groups/g";

	// groups
	// group 1: R1_work, sleep, and wander
	final public int[] R1_ROOM = { 0, 11, 15 };

	// weka
	final public String WEKA_DIR = "/weka/";
	// patterns
	final public String PATTERNS_DIR = "/p/";
	// mapping folder: clusters for each 2-length, and indices for a pair of
	// sensor ids and cluster id
	//m0: 0 - train index
	final public String PATTERNS_MAPPER_DIR = PATTERNS_DIR + "m/";
	//squential file: converted sensor events into sequential files for each activity
	final public String PATTERNS_SQUENTIAL_DIR = PATTERNS_DIR + "sq/";
//	e.g., sq/f/source0_1: 0 - training data id, 1 -  activity id
	final public String PATTERNS_SQUENTIAL_CONVERTED_FILES_DIR = PATTERNS_DIR + "sq/f/";
	// mapped test file; sq/t/source0_1:
	final public String PATTERNS_SQUENTIAL_CONVERTED_TEST_DIR = PATTERNS_DIR + "sq/t/";
	// e.g., sq/p/source0_1_2: 0-training data id, 1 - activity id, 2 - length of patterns
	final public String PATTERNS_SQUENTIAL_PATTERNS_DIR = PATTERNS_DIR + "sq/p/";
	//e.g., sq/sr/source0_1: 0 - training data id, 1 - activity id
	final public String PATTERNS_SCORE_DIR = PATTERNS_DIR + "sq/sr/";
	// inference result /sq/re/source0: 0- training data id
	final public String PATTERNS_RESULT_DIR = PATTERNS_DIR + "sq/re/";
	// recover patttersn to sensor sequence result /sq/rec/source0: 0- training data id
	final public String PATTERNS_RECOVER_DIR = PATTERNS_DIR + "sq/rec/";
}
