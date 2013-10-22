package cs6140.hw4;

import java.util.Comparator;

public class SolutionComparator implements Comparator<Solution> {

	@Override
	public int compare(Solution o1, Solution o2) {
		if(o1.getErrorRateWeighted()==o2.getErrorRateWeighted()){
			return 0;
		}else if(o1.getErrorRateWeighted()>o2.getErrorRateWeighted()){
			return 1;
		}else{
			return -1;
		}
	}

}
