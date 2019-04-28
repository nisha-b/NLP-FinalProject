package nlp.parser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Final Project Assignment
 * @author Nithya Deepak and Nisha Bhatia
 * @version 04/20/2019
 */
 
/**
 * This is a class to parse through tweets and make them parseable
 * Then the tweets are run through the Stanford parser to find their structure
 * Once their structure is found tweets are classified as gramatically correct or not
*/
public class TweetParser {
	public static HashMap<String, String> data = new HashMap<String, String>();
	public static HashMap<String, Double> components = new HashMap<String, Double>();
	public static HashMap<String, Double> counts = new HashMap<String, Double>();
	public static HashMap<String, Double> numTweets = new HashMap<String, Double>();
	
	// TAKEN FROM STACK OVERFLOW
	/**
	 * Remove accents and special characters
	 */
	public static String unaccent(String src) {
		return Normalizer
				.normalize(src, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}
	
	/**
	 * This is a method to clean a tweet and make it parseable
	 * non-alphanumeric characters, @, and RT's are removed
	 * @param String sentence.
	 * @return String.
	*/
	public static String cleanString(String sentence) {
		String[] splited = sentence.split("\\s+");
		String returnSentence = "";
		// for each word in each sentence
		for (String word : splited) {
			// if we have a url we don't want to add it to our data
			if (word.contains("http") || word.contains(".com") || word.contains("org") || word.contains(".gov") || word.contains(".net") || word.contains(".ly") || word.contains("www.")) {
				word = "url ";
			}
			// if word is tweeting a person
			// we just want a noun
			else if (word.contains("@")) { 
				word = "name ";
			}
			// if word is tweeting a person
			// we just want a noun
			else if (word.contains("#")) { 
				word = "hashtag ";
			}
			// if words is a RT
			// we just want the retweet
			else if (word.contains("RT")) { 
				word = word.replace("RT","") + " ";
			}
			// if the word contains special 
			else if (word.matches("[0-9A-Za-z]") == false) { 
				word = unaccent(word);
				word = word.replace("A","") + " ";
				
				
			}
			else {
				word = word + " ";
			}
			
			returnSentence += word;
		}
		
		// checking if the string is just punctuation
		String temp = returnSentence;
		temp = temp.replaceAll("[^A-Za-z0-9]", "");	
		temp = temp.replaceAll("\\s+","");
		temp = temp.trim();
		if (temp == "") {
			return "";
		}
		returnSentence = returnSentence.substring(0, returnSentence.length() - 1);
		return returnSentence + ".";
	}

	/**
	 * This is a method to get the grammar components the parser
	 * returned and add them to the counts component hashmap
	 * * @param String sentence.
	 * @return String.
	*/
	public static void addComponents(String parse) {
		String[] parses = parse.split("\\(");
		for (String indiv : parses) {
			indiv = indiv.replace(")", "");
			String[] realPattern = indiv.split("\\s");
			for (String word: realPattern) {
				word = word.replace(")", "");
				if (word.toUpperCase().equals(word) && word.matches("[A-Z]*")) {
					if (components.containsKey(word)) {
						Double temp = components.get(word);
						temp++;
						components.put(word, temp);
					}
					else {
						components.put(word, 1.0);
					}
				}
			}
		}
	}
	
	/**
	 * This is a method to get the grammar components the parser
	 * and check if it has a NN, NP and VP to be grammatical
	 * @param String sentence.
	 * @return String.
	*/
	public static boolean getIndividualParse(String parse) {
		ArrayList<String> list = new ArrayList<String>();
		String[] parses = parse.split("\\(");
		for (String indiv : parses) {
			indiv = indiv.replace(")", "");
			String[] realPattern = indiv.split("\\s");
			for (String word: realPattern) {
				word = word.replace(")", "");
				if (word.toUpperCase().equals(word) && word.matches("[A-Z]*")) {
					list.add(word);
				}
			}
		}
		if (list.contains("NP") && list.contains("VP") && list.contains("NN")) {
			return true;
		}
		return false;
	}
	
	// TAKEN FROM GEEKS FOR GEEKS TO SORT OUR HASHMAP
	public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) 
	{ 
		// Create a list from elements of HashMap 
		List<Map.Entry<String, Double> > list = 
			new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 

		// Sort the list 
		Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
			public int compare(Map.Entry<String, Double> o1, 
							Map.Entry<String, Double> o2) 
			{ 
				return (o2.getValue()).compareTo(o1.getValue()); 
			} 
		}); 
		
		// put data from sorted list to hashmap 
		HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
		for (Map.Entry<String, Double> aa : list) { 
			temp.put(aa.getKey(), aa.getValue()); 
		} 
		return temp; 
} 
	
	/**
	 * Main function to run the code
	*/
	public static void main(String[] args) throws IOException {
		
		// CODE TO CLEAN EACH TWEET AND OUTPUT IN A FILE TO PUT IN PARSER
		// adding all the filenames to an array for the data
//		ArrayList<String> filenames = new ArrayList<String>();
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/ellen.txt");
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/hillary.txt");
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/jimmy.txt");
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/obama.txt");
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/trump.txt");
//		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/cnn.txt");
//		
//		// cleaning data
//		for (String file : filenames) {
//			
//			// get name of the tweeter
//			String[] names = file.split("/");
//			String name = names[names.length-1];
//			name = name.replaceAll(".txt", "");
//			
//			PrintWriter out = new PrintWriter(name + ".txt");
//			
//			// reads in the file
//			BufferedReader buffer = new BufferedReader(new FileReader(file));
//			
//			String sentence = "";
//			// going sentence by sentence
//			while ((sentence = buffer.readLine()) != null) {
//			// call cleanString to clean the string off unparseable items
//				if (sentence != " ") {
//					sentence = cleanString(sentence);
//					out.println(sentence);
//					// add the parsed sentence to the hashmap
//					data.put(name, sentence);
//				}
//			}
//			out.close();
//			
//		} // for loop
		
		
		// CODE TO CREATE HASHMAPS OF THE SMALL DATA AND SEE MOST COMMON GRAMMARS TO CREATE NEW RULES
		// reads in the file
//		BufferedReader buffer = new BufferedReader(new FileReader("/Users/admin_deepak/Documents/corpus/final_project/data/small_output.txt"));
//					
//		String sentence = "";
//		// going sentence by sentence
//		while ((sentence = buffer.readLine()) != null) {
//			addComponents(sentence);
//		
//		}
//		
//		components = sortByValue(components);
//		
//		for (String word : components.keySet()) {
//			System.out.println("word " + word + " values " + components.get(word));
//		}
//		
//		// After everything has been counted, sort the hashmap
//		components = sortByValue(components);
//		
//		for (String word : components.keySet()) {
//			System.out.println(word + " " + counts.get(word));
//		}
//		
//			
		
		
		// CODE TO GO THROUGH THE OUTPUTED DATA AND SEE TOP TWEETS
		// adding all the filenames to an array for the data
		ArrayList<String> filenames = new ArrayList<String>();
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/cnn_output.txt");
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/ellen_output.txt");
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/hillary_output.txt");
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/obama_output.txt");
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/trump_output.txt");
		filenames.add("/Users/admin_deepak/Documents/corpus/final_project/data/jimmy_output.txt");
		
		for (String file : filenames) {

			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String sentence = "";
			
			//  get name of the tweeter
			String[] names = file.split("/");
			String name = names[names.length-1];
			name = name.replaceAll(".txt", "");
			name = name.replaceAll("_output", "");
			
			// going sentence by sentence
			while ((sentence = buffer.readLine()) != null) {
				// gets whether the sentence was gramatically correct or not
				boolean result = getIndividualParse(sentence);
				// add to the counter of tweets
				if (numTweets.containsKey(name)) {
					double val = numTweets.get(name);
					val++;
					numTweets.put(name, val);
				}
				else {
					numTweets.put(name, 1.0);
				}
				// if it was grammatical
				if (result == true) {
					// add it to the counts hashmap
					if (counts.containsKey(name)) {
						double val = counts.get(name);
						val++;
						counts.put(name, val);
					}
					else {
						counts.put(name, 1.0);
					}
				}
			}
		}
		
		// Change the number to the probability of gramatically correct tweets
		for (String tweeter : counts.keySet()) {
			double denom = numTweets.get(tweeter);
			double numer = counts.get(tweeter);
			double newAns = numer/denom;
			
			counts.put(tweeter, newAns);
			
		}
		
		// After everything has been counted, sort the hashmap
		counts = sortByValue(counts);
		
		for (String word : counts.keySet()) {
			System.out.println(word + " tweets are grammatically correct " + counts.get(word) + "% of the time");
		}
		
		
	} // main	
} // end