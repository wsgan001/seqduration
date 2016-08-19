package patternselection;

import java.io.FileNotFoundException;
import java.util.List;

public class InformationGainEvaluator extends PatternEvaluatorAbs implements PatternEvaluator {

	public InformationGainEvaluator(String a_db_file, int the_sizeOfClasses) throws FileNotFoundException {
		super(a_db_file, the_sizeOfClasses);
	}

	@Override
	public double getScore(String a_pattern) {
		double[] classRatio = getClassRatio();
		calcPatternRatio(a_pattern);
		double[][] patternRatio = getConditionalRatioOnPatterns(a_pattern);
		double first=0, second=0, third=0;
		for(int i=0; i< my_sizeOfClasses; i++) {
			first += classRatio[i] * Math.log(classRatio[i]);
			second +=patternRatio[0][i]*Math.log(patternRatio[0][i]);
			third +=patternRatio[1][i]*Math.log(patternRatio[1][i]);
		}
		second *= patternRatio[0][my_sizeOfClasses];
		third *= patternRatio[1][my_sizeOfClasses];
		return -1 * first + second + third;
	}

}
