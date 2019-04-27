package nlp.parser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Word Alignment Assignment
 * @author Nithya Deepak and Nisha Bhatia
 * @version 04/01/2019
 */
 
/**
 * This is a class to calculate the Naive-Bayes probability of sentences
 * To predict if a sentiment is positive or negative
*/


public class NBClassifier {
	
	public static HashMap<String, HashMap<String, Double>> lambda = new HashMap<String, HashMap<String, Double>>();
	public static int sentences = 0;
	public static HashMap<String, HashMap<String, Double>> counts = new HashMap<String, HashMap<String, Double>>();
	public static HashMap<String, Double> labels = new HashMap<String, Double>();
	public static HashMap<String, String> results = new HashMap<String, String>();
	public static HashMap<String, Double> values = new HashMap<String, Double>();
	public static Set<String> unique = new HashSet<String>();
	public static HashMap<String, Double> forPrintingPos = new HashMap<String, Double>();
	public static HashMap<String, Double> forPrintingNeg = new HashMap<String, Double>();
	
	
//	// TAKEN FROM GEEKSFORGEEKS: IN ORDER TO HELP WITH THE TOP SORTING
//	public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) 
//		{ 
//			// Create a list from elements of HashMap 
//			List<Map.Entry<String, Double> > list = 
//				new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 
//
//			// Sort the list 
//			Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
//				public int compare(Map.Entry<String, Double> o1, 
//								Map.Entry<String, Double> o2) 
//				{ 
//					return (o2.getValue()).compareTo(o1.getValue()); 
//				} 
//			}); 
//			
//			// put data from sorted list to hashmap 
//			HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
//			for (Map.Entry<String, Double> aa : list) { 
//				temp.put(aa.getKey(), aa.getValue()); 
//			} 
//			return temp; 
//	} 
	
	
	/**
		 * This is the method that runs the file
		 * Split into 2 parts- training and then testing
		 * Does the Naive Bayes probability for each word in each sentence compared to the training data
		 * To predict if a sentence is positive or negative
		 * @param Nothing.
		 * @return Nothing.
	*/
	public static void main(String[] args) throws IOException {
		
		// gets the inputs from the user such as the training dataset, testing dataset and the lambda
		double lambda = 0.0;
		
		// TRAINING THE DATA
		// reads in the file
		String csvFile = "/Users/nishabhatia/Documents/workspace/finalProject/data/poli.csv";
        String csvSplitBy = ",";
		BufferedReader buffer = new BufferedReader(new FileReader(csvFile));
		String sentence = "";
		// going sentence by sentence
		while ((sentence = buffer.readLine()) != null) {
			//get csv results as label, tweet
			//System.out.println(sentence);
			String[] splited1 = sentence.split(csvSplitBy);
			if (splited1.length == 2) {
//			for (String x: splited1) {
//				System.out.println(x);
//			}
			// save label
			String label = splited1[0];
			//get text of tweet
			String splited2 = splited1[1];
			//split text of tweet by space
			String[] splited = splited2.split("\\s+");
			
			// for each word in each sentence
			for (String word : splited) {
				HashMap <String, Double> inner;
				// if we are at the label we don't want to add that to our counts
				if (word == label) {
					continue;
				}
				// if we have already seen this, add to the counts
				if (counts.containsKey(label)) {
					inner = counts.get(label);
					if (inner.containsKey(word)) {
						Double count = inner.get(word);
						count++;
						inner.put(word, count);
					}
					else {
						inner.put(word, 1.0);
					}
				}
				// else create a new inner hashmap for it
				else {
					inner = new HashMap<String, Double>();
					inner.put(word, 1.0);
				}
				counts.put(label, inner);

				// add each word's label to the label hashmap
				// if it exists already just increase the counts
				if (labels.containsKey(label)) {
					Double count = labels.get(label);
					count++;
					labels.put(label, count);
				}
				else {
					labels.put(label, 1.0);
				}
				// add each word to the unique hashmap
				unique.add(word);
			}
			}
		}
		
		//LAMBDA CLASSIFICATION: NAIVE BAYS PROBABILITY OF EACH WORD IN EACH SENTENCE
		
		
		BufferedReader buffer1 = new BufferedReader(new FileReader("/Users/nishabhatia/Documents/workspace/finalProject/data/classifyHillary.data"));
		String sentence1 = "";
		// sentence by sentence
		while ((sentence1 = buffer1.readLine()) != null) { 
			// define current and denom to 0
			double current = 0.0;
			String[] splited = sentence1.split("\\s+");
			double denom = 0.0;
			String bestLabel = "";
			
			// for each word in each sentence
			for (String s : splited) {
				// for each label: since we need to do this for both positive and negative
				for (String label: labels.keySet()) {
					denom = labels.get(label);
					// Getting the number of words in that label
					if (values.containsKey(label)) {
						current = values.get(label);
					}
					else {
						current = 0.0;
					}
					// if this word is in either dataset
					if (unique.contains(s) == true) {
						// get the naive-bays probability including the number of times it appeared
						if (counts.get(label).containsKey(s)) {
							current += Math.log10((counts.get(label).get(s) + lambda) / (lambda*unique.size() + denom));
						}
						// get the naive-bays probability without the number of times it appeared
						else {
							current += Math.log10((lambda) / (lambda*unique.size() + denom));
						}
						// put the value under the correct label
						values.put(label, current);
						if (label.compareTo("negative") == 0) {
							
							forPrintingNeg.put(s, current);
						}
						if (label.compareTo("positive") == 0) {
							
							forPrintingPos.put(s, current);
						}
						current = 0.0;
						denom = 0.0;
						
					}
					
				}
			}
			// for each of the values for the sentence, add the probability of 
			// the label
			for (String val : values.keySet()) {
				double value = values.get(val);
				value += Math.log10(labels.get(val)/labels.size());
				values.put(val, value);
			}
			
			// comparing the values between the labels
			// then gets the max of the values
			Map.Entry<String, Double> maxEntry = null;
			for (Map.Entry<String, Double> entry : values.entrySet())
			{
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) >= 0)
			    {
			    	bestLabel = entry.getKey();
			        maxEntry = entry;
			    }
			}
			results.put(sentence1, bestLabel);
			values.clear();
		}
		
		// CODE FOR COMPARING PROBABILITIES
		BufferedReader buffer2 = new BufferedReader(new FileReader("/Users/nishabhatia/Documents/workspace/finalProject/data/test.csv"));
		String sentence2 = "";
		int counters = 0;
		int count = 0;
		while ((sentence2 = buffer2.readLine()) != null) {
			String[] splited1 = sentence2.split(csvSplitBy);
			if (splited1.length == 2) {
				String[] splited = sentence2.split("\\s+");
				ArrayList<String> list = new ArrayList<String>();
				for (String x: splited) {
					list.add(x.trim());
				}
				count++;
				System.out.println("string no " + count);
				String firstItem = list.get(0);
				String[] fix = firstItem.split(",");
				if (fix.length == 2) {
					String label = fix[0];
					String first = fix[1];
	//				System.out.println(secondItem);
	//				String modifiedSecond = " " + secondItem;
					list.remove(0);
					list.add(0, label);
					list.add(1, first);
					//System.out.println("list CORRECT " + list.toString());
					for (String name: results.keySet()){
	//		            String key = splited1[1];
			            String[] splited2 = name.split("\\s+");
			            ArrayList<String> makeList = new ArrayList<String> (); 
			            for (String x: splited2) {
							makeList.add(x.trim());
						}
			            String value = results.get(name);
			            makeList.add(0, value);
			            //System.out.println("makeList " + makeList.toString());
			            
			            if (makeList.equals(list)) {
			            	System.out.println("incremented counter");
			            	counters++;
			            	continue;
			            }
					}
				}
			}
		}
		System.out.println(counters);
		
		
		
		
		// Goes through the results hashmap
		// and prints each sentence and what it was classified as
		for (String name: results.keySet()){
            String key = name.toString();
            String value = results.get(name);  
            System.out.println(key + "	|	" + value);  
		}
		
		
//		TO PRINT THE TOP RESULTS
//		HashMap<String, Double> sortedPos = sortByValue(forPrintingPos);
//		HashMap<String, Double> sortedNeg = sortByValue(forPrintingNeg);
//		int count =0;
//		System.out.println("TOP POSITIVE");
//		for (String name: sortedPos.keySet()){
//            String key = name.toString();
//            double value = sortedPos.get(name);  
//            if (count == 20) {
//            	break;
//            }
//            System.out.println(key + "	|	" + value);
//            count++;
//		}
//		count = 0;
//		System.out.println("TOP NEGATIVE");
//		for (String name: sortedNeg.keySet()){
//            String key = name.toString();
//            double value = sortedNeg.get(name);  
//            if (count == 20) {
//            	System.exit(0);
//            }
//            System.out.println(key + "	|	" + value);
//            count++;
//		}
//		for(String x: counts.keySet()) {
//			HashMap<String, Double> inner = counts.get(x);
//			System.out.println(x);
//			for (String name: inner.keySet()){
//	            String key = name.toString();
//	            double value = inner.get(name);  
//	            System.out.println(key + " " + value);  
//			} 
//		}
	} // main
} // end