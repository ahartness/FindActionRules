import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Main {
	private static final String Arraylist = null;

	static Scanner input;
	
	public static List<String> attributeNames = new ArrayList<String>();
	public static List<String> stableAttributes = new ArrayList<String>();
	public static List<String> flexibleAttributes = new ArrayList<String>();
	public static List<String> actionRules = new ArrayList<String>();
	
	public static String decisionAttribute,decisionFrom,decisionTo;
	
	
	public static ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(); 
	
	static Map<String, HashSet<String>> distinctAttributeValues = new HashMap<String, HashSet<String>>();
	static Map<HashSet<String>, HashSet<String>> attributeValues = new HashMap<HashSet<String>, HashSet<String>>();
	static Map<HashSet<String>, HashSet<String>> reducedAttributeValues = new HashMap<HashSet<String>, HashSet<String>>();
	static Map<String, HashSet<String>> decisionValues = new HashMap<String, HashSet<String>>();
	static Map<ArrayList<String>, HashSet<String>> markedValues = new HashMap<ArrayList<String>, HashSet<String>>();
	public static Map<ArrayList<String>,String> certainRules = new HashMap<ArrayList<String>,String>();
	public static Map<ArrayList<String>,HashSet<String>> possibleRules = new HashMap<ArrayList<String>,HashSet<String>>();
	
	public static void main(String[] args) {
		//Read the attribute names
		readAttributes();
		
		//Read data
		readData();
		
		//Read stable,flexible and decision attributes
		setStableAttributes(attributeNames);
		
		//Find Certain and Possible rules
		findRules();
		
		printActionRules(actionRules);
		
	}

	//Printing String, List and Map
	public static void printMessage(String content){
		System.out.println(content);
	}
	
	public static void printList(List<String> list){
		Iterator iterate = list.iterator();
		
		while(iterate.hasNext()){
			printMessage(iterate.next().toString());
		}
	}
	
	private static void printAttributeMap(Map<HashSet<String>, HashSet<String>> values) {
		for(Map.Entry<HashSet<String>, HashSet<String>> set : values.entrySet()){
			printMessage(set.getKey().toString() + " = " + set.getValue());
		}
	}
	
	private static void printCertainRulesMap(Map<ArrayList<String>, String> value) {
		printMessage("\nCertain Rules:");
		for(Map.Entry<ArrayList<String>,String> set : value.entrySet()){
			int support = calculateSupportLERS(set.getKey(),set.getValue());
			String confidence = calculateConfidenceLERS(set.getKey(),set.getValue());
			
			printMessage(set.getKey().toString() + " -> " + set.getValue() + "[Support:-" + support + ", Confidence:-" + confidence +"%]");
			
		}
	}
	
	private static void printPossibleRulesMap(Map<ArrayList<String>, HashSet<String>> value) {
		
		if(!value.isEmpty()){
			printMessage("\nPossible Rules:");
			for(Map.Entry<ArrayList<String>,HashSet<String>> set : value.entrySet()){
				for(String possibleValue:set.getValue()){
					int support = calculateSupportLERS(set.getKey(),possibleValue);
					String confidence = calculateConfidenceLERS(set.getKey(),possibleValue);
					
					printMessage(set.getKey().toString() + " -> " + possibleValue + "[Support:-" + support + ", Confidence:-" + confidence +"%]");
				}

			}
		}
	}
	
	private static void printActionRules(List<String> ar){
		
		printMessage("\nAction Rules:");
		printMessage("--------------------------\n");
		
		if(!ar.isEmpty()){
			
			
			for(String temp : ar){
				printMessage(temp);
			}
		}
	}

	private static int findLERSSupport(ArrayList<String> tempList) {
		int count = 0;
		
		for(ArrayList<String> data : data){	
			if(data.containsAll(tempList))
				count++;
		}
		
		return count;
	}
	
	private static int calculateSupportLERS(ArrayList<String> key, String value) {
		ArrayList<String> tempList = new ArrayList<String>();
		
		for(String val : key){
			tempList.add(val);
		}
		
		if(!value.isEmpty())
			tempList.add(value);
	
		return findLERSSupport(tempList);
		
	}


	private static String calculateConfidenceLERS(ArrayList<String> key,
			String value) {
		int num = calculateSupportLERS(key, value);
		int den = calculateSupportLERS(key, "");
		int confidence = (num * 100)/den;
		
		return String.valueOf(confidence);
	}
	
	//Reading attributes and data
	private static void readAttributes() {
		try {
			input = new Scanner(new File("attribute.txt"));
			
			while (input.hasNext()) {
				attributeNames.add(input.next());		
			}
			
		} catch (FileNotFoundException e) {
			printMessage("File Not Found");
			e.printStackTrace();
		}
		
	}
	
	private static void readData() {
		try {
			input = new Scanner(new File("data.txt"));
			int lineNo = 0;
			
			while(input.hasNextLine()){
				String[] lineData = input.nextLine().split("\\s+");
				String key;
				
				lineNo++;
				ArrayList<String> tempList = new ArrayList<String>();
				HashSet<String> set;
				
				for (int i=0;i<lineData.length;i++) {
					String currentAttributeValue = lineData[i];
					String attributeName = attributeNames.get(i);
					key = attributeName + currentAttributeValue;
					
					tempList.add(key);

					HashSet<String> mapKey = new HashSet<String>();
					mapKey.add(key);
					setMap(attributeValues,lineData[i],mapKey,lineNo);
					
					if (distinctAttributeValues.containsKey(attributeName)) {
						set = distinctAttributeValues.get(attributeName);
						set.add(key);
						
					}else{
						set = new HashSet<String>();
					}
					
					set.add(key);
					distinctAttributeValues.put(attributeName, set);
				}
		
				data.add(tempList);
			}
			
			
		} catch (FileNotFoundException e) {
			printMessage("File Not Found");
			e.printStackTrace();
		}
	}

	private static void setMap(Map<HashSet<String>, HashSet<String>> values,
			String string, HashSet<String> key, int lineNo) {
		HashSet<String> tempSet;
		
		if (values.containsKey(key)) {
			tempSet = values.get(key);						
		}else{
			tempSet = new HashSet<String>();
		}
		
		tempSet.add("x"+lineNo);
		values.put(key, tempSet);
	}

	private static void setStableAttributes(List<String> attributes) {
		printMessage("\nPlease Give a choice\n1.Enter Stable Attributes...\n2.Go next...");
		
		input = new Scanner(System.in);
		int choice = input.nextInt();
		if(choice==1){
			printMessage("\nAttributes Available");
			printMessage("--------------------------");
			printList(attributes);
			printMessage("Enter a stable attribute name...");
			
			String userStableAttribute = input.next();
			if(checkValid(attributes,userStableAttribute)){
				stableAttributes.addAll(distinctAttributeValues.get(userStableAttribute));
				attributes.remove(userStableAttribute);
			}else{
				printMessage("Invalid Attribute name...\n");
			}
			
			setStableAttributes(attributes);
		}
		
		else if(choice==2){
			setDecisionAttribute(attributes);
		}
		
		else{
			printMessage("\nPlease Enter 1 or 2");
			setStableAttributes(attributes);
		}
	}

	private static boolean checkValid(List<String> attributes,String userStableAttribute) {
		if(attributes.contains(userStableAttribute))
			return true;
		else return false;
	}

	private static void setDecisionAttribute(List<String> attributes) {
		printMessage("\nAttributes Available");
		printMessage("--------------------------");
		printList(attributes);
		printMessage("Enter a decision attribute name...");
		
		input = new Scanner(System.in);
		decisionAttribute = input.next();
		
		if (checkValid(attributes,decisionAttribute)) {
			attributes.remove(decisionAttribute);
			flexibleAttributes = attributes;
			
			HashSet<String> decisionValues = distinctAttributeValues.get(decisionAttribute);
			removeDecisionValueFromAttributes(decisionValues);
			
		}else{
			printMessage("Invalid attrbibute.");
			setDecisionAttribute(attributes);
		}
		
	}

	private static void removeDecisionValueFromAttributes(HashSet<String> decisionValues) {
		for(String value : decisionValues){
			HashSet<String> newHash = new HashSet<String>();
			newHash.add(value);
			Main.decisionValues.put(value, attributeValues.get(newHash));
			attributeValues.remove(newHash);
		}
	}

	private static void findRules() {
		int loopCount = 0;
		
		while(!attributeValues.isEmpty()){
			printMessage("\nLoop " + (++loopCount) +":");
			printMessage("--------------------------");
			printAttributeMap(attributeValues);
			
			for (Map.Entry<HashSet<String>, HashSet<String>> set : attributeValues.entrySet()) {
				ArrayList<String> setKey = new ArrayList<String>();
				setKey.addAll(set.getKey());
				
				if (set.getValue().isEmpty()) {
					continue;
				}else{
					for(Map.Entry<String, HashSet<String>> decisionSet : decisionValues.entrySet()){
						if(decisionSet.getValue().containsAll(set.getValue())){
							certainRules.put(setKey, decisionSet.getKey());
							markedValues.put(setKey, set.getValue());
							break;
						}
					}
				}
				
				if(!markedValues.containsKey(setKey)){
					HashSet<String> possibleRulesSet = new HashSet<String>();
					for(Map.Entry<String, HashSet<String>> decisionSet : decisionValues.entrySet()){
						possibleRulesSet.add(decisionSet.getKey());
					}
					possibleRules.put(setKey, possibleRulesSet);
				}
				
			}
					
			
			
			removeMarkedValues();
			
			printCertainRulesMap(certainRules);
			//printPossibleRulesMap(possibleRules);
			
			combinePossibleRules();
			findActionRules();
		}
	}
	
	private static void findActionRules(){
		
		//printMessage(markedValues.toString());
		//printMessage(decisionValues.toString());

		boolean flag = true;
		
		while(flag){
			ArrayList<String> tempSet = new ArrayList<String>();
			for (Map.Entry<ArrayList<String>, HashSet<String>> set1 : markedValues.entrySet()){
				tempSet = set1.getKey();
				
				for(Map.Entry<ArrayList<String>, HashSet<String>> set2 : markedValues.entrySet()){
					for(String item1 : tempSet){
						for(String item2 : set2.getKey()){		
							if(item1.equals(item2)){
								break;
							}
							if(item1.contains(item2.substring(0, 1))){
								if(item1.contains(item2.substring(1, 2))){
									break;
								}
								else{
									for(Map.Entry<String, HashSet<String>> decSet : decisionValues.entrySet()){
										if(decSet.getValue().containsAll(set1.getValue()) || !decSet.getValue().containsAll(set2.getValue())){
											
											//if(actionRules.contains(tempString));
											flag = false;
											
										}else{
											//printMessage(item1.toString() + " " + item2.toString());
											int num1 = Integer.parseInt(item1.substring(1, 2));
											int num2 = Integer.parseInt(item2.substring(1, 2));
											int num3 = Integer.parseInt(decSet.getKey().substring(1,2));
											int num4 = num3 - 1;
											

											
											if(num1 < num2){
												
												String tempString = "(" + item1.substring(0, 1)+ ", " + num2 + " --> " + num1 
														+ ")";
												
												if(set1.getKey().size() > 1){
													for(String item : set1.getKey()){
														if(item == item1){
															break;
														}
														
														tempString += " ^ (" + item.substring(0, 1) + " = " + item.substring(1,2) + ")";
													}
												}
												tempString += " --> (" + decSet.getKey().substring(0, 1) + ", " + num4 +
														" --> " + num3 + ")";
												if(actionRules.contains(tempString)){
													break;
												}
												else{
													
													actionRules.add(tempString);
												}
											}
											
											flag =  false;
										}
									}
								}
							}
						}
					}
					
					
				}
			}
			
		}
		
		
	}

	private static void removeMarkedValues() {
		for(Map.Entry<ArrayList<String>, HashSet<String>> markedSet : markedValues.entrySet()){
			attributeValues.remove(new HashSet<String>(markedSet.getKey()));
		}
		
	}
	
	private static void combinePossibleRules() {
		Set<ArrayList<String>> keySet = possibleRules.keySet();
		ArrayList<ArrayList<String>> keyList = new ArrayList<ArrayList<String>>();
		keyList.addAll(keySet);
		
		for(int i = 0;i<possibleRules.size();i++){
			for(int j = (i+1);j<possibleRules.size();j++){
				HashSet<String> combinedKeys = new HashSet<String>(keyList.get(i));
				combinedKeys.addAll(new HashSet<String>(keyList.get(j)));
				
				if(!checkSameGroup(combinedKeys)){
					combineAttributeValues(combinedKeys);
				}
			}
		}
		
		possibleRules.clear();
		
		removeRedundantValues();
		clearAttributeValues();
		
	}

	private static boolean checkSameGroup(HashSet<String> combinedKeys) {
		for(Map.Entry<String, HashSet<String>> singleAttribute : distinctAttributeValues.entrySet()){
			if(singleAttribute.getValue().containsAll(combinedKeys)){
				return true;
			}
		}
		
		return false;
	}
	
	private static void combineAttributeValues(HashSet<String> combinedKeys) {
		HashSet<String> combinedValues = new HashSet<String>();
			
		for(Map.Entry<HashSet<String>, HashSet<String>> attributeValue : attributeValues.entrySet()){
			if(combinedKeys.containsAll(attributeValue.getKey())){
				if(combinedValues.isEmpty()){
					combinedValues.addAll(attributeValue.getValue());
				}else{
					combinedValues.retainAll(attributeValue.getValue());
				}
			}
		}
		reducedAttributeValues.put(combinedKeys, combinedValues);
	
	}

	private static void removeRedundantValues() {
		HashSet<String> mark = new HashSet<String>();
		
		for(Map.Entry<HashSet<String>, HashSet<String>> reducedAttributeValue : reducedAttributeValues.entrySet()){
			for(Map.Entry<HashSet<String>, HashSet<String>> attributeValue : attributeValues.entrySet()){
				
				if(attributeValue.getValue().containsAll(reducedAttributeValue.getValue()) || reducedAttributeValue.getValue().isEmpty()){
					mark.addAll(reducedAttributeValue.getKey());
				}
			}
		}
		
		reducedAttributeValues.remove(mark);
		
		
	}
	
	private static void clearAttributeValues() {
		 attributeValues.clear();
		 for(Map.Entry<HashSet<String>, HashSet<String>> reducedAttributeValue : reducedAttributeValues.entrySet()){
			 attributeValues.put(reducedAttributeValue.getKey(), reducedAttributeValue.getValue());
		 }
		 reducedAttributeValues.clear();
	}

}
