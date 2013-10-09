package cs6140.hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class KCrossValidation {

	public ArrayList<Email> getRandomTrainingData(){
	      ArrayList<Email> result=new ArrayList<Email>();
	      Random r=new Random();
	      int initSize = trainingData.size();
	      System.out.println(initSize);
	      for(int i=0;i<initSize;i++){
	    	  int size=trainingData.size();
	    	  int randomIndex=r.nextInt(size);
	    	  result.add(trainingData.remove(randomIndex));
	      }
	      
	      trainingData=result;
	      System.out.println("Kcross"+trainingData.size());
	      return trainingData;
	      
	}
	
	public ArrayList<Email> getTrainingData() {
		return trainingData;
	}

	public void setTrainingData(ArrayList<Email> trainingData) {
		this.trainingData = trainingData;
	}

	public ArrayList<Email> getTestingData() {
		return testingData;
	}

	public void setTestingData(ArrayList<Email> testingData) {
		this.testingData = testingData;
	}

	/**
	 * @param args
	 */
	private int k;
	private ArrayList<ArrayList<String>> partitionedFolds = new ArrayList<ArrayList<String>>();
	private HashMap<Integer, ArrayList<Email>> vectorFoldsMap = new HashMap<Integer, ArrayList<Email>>();
	private ArrayList<Email> trainingData = new ArrayList<Email>();
	private ArrayList<Email> testingData = new ArrayList<Email>();

	public KCrossValidation(int k) {
		this.k = k;
		readFromGivenURL(MyConstant.DATA_PATH);
	}

	private void readFromGivenURL(String url)  {
		File file = new File(url);
		initPartitionedFolds();
		try {
			parseFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		groupRawData();
	}

	private void groupRawData() {
		for (int i = 0; i < partitionedFolds.size(); i++) {
			vectorFoldsMap.put(i,
					transformToFeatureVec(partitionedFolds.get(i)));
		}
	}

	private ArrayList<Email> transformToFeatureVec(
			ArrayList<String> fold) {
		ArrayList<Email> foldWithVectorFeature = new ArrayList<Email>();
		for (String str : fold) {
			String[] features = str.split(",");
			Email vec_features = new Email();
			for (String strFrequency : features) {
				vec_features.add(Double.parseDouble(strFrequency));
			}
			foldWithVectorFeature.add(vec_features);
		}
		return foldWithVectorFeature;
	}

	private void parseFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader bf = new BufferedReader(fr);
		String str = "";
		int count = 0;
		while ((str = bf.readLine()) != null) {
			int fold_index = count % k;
			partitionedFolds.get(fold_index).add(str);
			count++;
		}
	}

	private void initPartitionedFolds() {
		partitionedFolds.clear();
		for (int i = 0; i < k; i++) {
			partitionedFolds.add(new ArrayList<String>());
		}
	}

	public void extractTestingSetByIndex(int indexOfTestingData) {
		trainingData.clear();
		testingData.clear();
		Iterator<Integer> it = vectorFoldsMap.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			ArrayList<Email> emailsInOneFold = vectorFoldsMap.get(key);
            if(key == indexOfTestingData)
            	testingData.addAll(emailsInOneFold);
            else
            	trainingData.addAll(emailsInOneFold);
            
		}
	}

	public static void main(String[] args) throws IOException {
		KCrossValidation kv = new KCrossValidation(10);
		kv.readFromGivenURL(MyConstant.DATA_PATH);
	}
}
