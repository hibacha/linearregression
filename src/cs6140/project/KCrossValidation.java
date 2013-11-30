package cs6140.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import cs6140.project.Car;

public class KCrossValidation {

	public static String PATH="/Users/zhouyf/Dropbox/machine HW/finalProject/car.data";
	public ArrayList<Car> getRandomTrainingData(){
	      ArrayList<Car> result=new ArrayList<Car>();
	      Random r=new Random();
	      int initSize = trainingData.size();
	      for(int i=0;i<initSize;i++){
	    	  int size=trainingData.size();
	    	  int randomIndex=r.nextInt(size);
	    	  result.add(trainingData.remove(randomIndex));
	      }
	      
	      trainingData=result;
	      return trainingData;
	      
	}
	

	public ArrayList<Car> getTrainingData() {
		return trainingData;
	}

	public void setTrainingData(ArrayList<Car> trainingData) {
		this.trainingData = trainingData;
	}

	public ArrayList<Car> getTestingData() {
		return testingData;
	}

	public void setTestingData(ArrayList<Car> testingData) {
		this.testingData = testingData;
	}

	/**
	 * @param args
	 */
	private int k;
	private ArrayList<ArrayList<String>> partitionedFolds = new ArrayList<ArrayList<String>>();
	private HashMap<Integer, ArrayList<Car>> vectorFoldsMap = new HashMap<Integer, ArrayList<Car>>();
	private ArrayList<Car> trainingData = new ArrayList<Car>();
	private ArrayList<Car> testingData = new ArrayList<Car>();

	public KCrossValidation(int k) {
		this.k = k;
		readFromGivenURL(PATH);
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

	private ArrayList<Car> transformToFeatureVec(
			ArrayList<String> fold) {
		ArrayList<Car> foldWithVectorFeature = new ArrayList<Car>();
		for (String str : fold) {
			String[] features = str.split(",");
			Car vec_features = new Car();
			if(features.length!=7){
				System.err.println("check format not enough value");
			}
			
//			car.setBuying(Level.generateEnumByString(features[0]));
//			car.setMaint(Level.generateEnumByString(features[1]));
//			car.setDoors(Number.generateEnumByString(features[2]));
//			car.setPersons(Number.generateEnumByString(features[3]));
//			car.setLug(Size.generateEnumByString(features[4]));
//			car.setSafety(Level.generateEnumByString(features[5]));
//			car.setCarClass(CarClass.generateEnumByString(features[6]));
			
			for (String strFrequency : features) {
				vec_features.add(strFrequency);
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
			ArrayList<Car> emailsInOneFold = vectorFoldsMap.get(key);
            if(key == indexOfTestingData)
            	testingData.addAll(emailsInOneFold);
            else
            	trainingData.addAll(emailsInOneFold);
            
		}
	}

}
