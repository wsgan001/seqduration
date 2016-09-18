package v2.data;

public interface Parameters {
	
	final public String SOURCE = "source";
	
	final public String SPLIT = "split";
	
	final public String RAW_MAPPING = "data/segments/raw-association";

	final public String ACT_SPLIT_DIR = "data.v2/"+SOURCE+"/a";
	// split the association into minitues
	final public String ACT_SPLIT_MIN_DIR = "data.v2/"+SPLIT+"/a";
	// number of folders
	final public int NUM_OF_FOLDERS = 10;
	// grouping of activities
	final public String GROUPS_DIR = "data.v2/groups/g";
	
	// groups
	//group 1: R1_work, sleep, and wander
	final public int[] R1_ROOM = { 0, 11, 15 };


}
