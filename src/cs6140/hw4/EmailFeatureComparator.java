package cs6140.hw4;

import java.util.Comparator;

import cs6140.hw4.Email;

public class EmailFeatureComparator implements Comparator<Email>{
    
	private int featureIndex;
	public EmailFeatureComparator(int featureIndex){
		this.featureIndex=featureIndex;
	}
	
	public int getFeatureIndex() {
		return featureIndex;
	}

	public void setFeatureIndex(int featureIndex) {
		this.featureIndex = featureIndex;
	}

	@Override
	public int compare(Email o1, Email o2) {
		if(o1.get(featureIndex)==o2.get(featureIndex)){
			return 0;
		}else if(o1.get(featureIndex) > o2.get(featureIndex)){
			return 1;	
		}else{
			return -1;
		}
	}

}
