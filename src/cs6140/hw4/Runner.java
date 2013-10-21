package cs6140.hw4;

import java.util.ArrayList;

import cs6140.hw3.Email;

public class Runner {

	public static void main(String[] args) {
		
		KCrossValidation k = new KCrossValidation(10);
		k.extractTestingSetByIndex(0);
		ArrayList<Email> trainingSet = k.getTrainingData();
		normalize(trainingSet);

	}

	public static void normalize(ArrayList<Email> trainingSet) {
		for (int i = 0; i < trainingSet.size(); i++) {
			if (trainingSet.get(i).get(MyConstant.INDEX_EMAIL_SPAM_LABEL) == 0) {
				trainingSet.get(i).set(MyConstant.INDEX_EMAIL_SPAM_LABEL, -1.0);
			}
			trainingSet.get(i).add(new Double(i));
		}
	}
}
