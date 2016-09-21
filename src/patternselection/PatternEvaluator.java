package patternselection;

import java.util.List;

public interface PatternEvaluator {
	/**
	 * 
	 * @param a_pattern a pattern consists of 2-length sequence pattern ids.
	 * @return a significance score
	 */
	public double getScore(final String a_pattern, double[] scor);
}
