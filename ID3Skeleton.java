package for_id3a;

//Heather Myers
//9/23/15
//CS 280
//Dave Thau
//Homework 1 - section 3 part 

import java.io.*;
import java.util.*;



public class ID3Skeleton {
  int numAttributes; // The number of attributes.
  String[] attributeNames; // The names of all attributes.
  private int attributeClass = 0; // The index of the class attribute.

  /* 
    The attributes variable contains the possible values for each attribute.
    For example, attributes.get(0) contains the values of the 0-th attribute.
  */
  ArrayList<ArrayList<String>> attributes;

  /*  
    The class to represent a training instance.
  */
  class Instance {
    public String[] attributes;
    public Instance(int numAttributes) {
      attributes = new String[numAttributes];
    }
  };

  /* The class to represent a node in the tree. */
  class Node {
    // The entropy of the instances.
    public double entropy; 

    // The set of instances of this node.
    public ArrayList<Instance> data;

    // If this isn't a leaf node, the attribute used to divide the node.
    public int splitAttribute;

    // The attribute value used to create this node.
    // This is the value of the parent's splitAttribute that led to this
    // node being created.
    public String splitValue;

    // References to child nodes.
    public Node[] children;

    // The parent of this node (root has parent of null).
    public Node parent;

    // The constructor.
    public Node() {
      data = new ArrayList<Instance>();
    }

  };

  // The root of the tree. 
  Node root = new Node();

  /*
    Given a set of instances and an attribute, return the 
    values of that attrbute for those instances.
  */
  public ArrayList<String> getAllValues(ArrayList<Instance> data, 
                                        int attribute) {
    ArrayList<String> values = new ArrayList<String>();
    for (int i = 0; i < data.size(); i++) {
      Instance instance = (Instance) data.get(i);
      String value = (String) instance.attributes[attribute];
      int index = values.indexOf(value);
      if (index < 0) {
        values.add(value);
      }
    }
    return values;
  }

  /*
    Given a set of instances, return the majority class.
  */
  public String majorityClass(ArrayList<Instance> data) {
    HashMap<String, Integer> tracker = new HashMap<String, Integer>();
    String majorityClass = "";
    Iterator<Instance> iter = data.iterator();
    while(iter.hasNext()) {
      Instance point = iter.next();
      String value = point.attributes[attributeClass];
      if (tracker.containsKey(value)) {
	Integer thisCount = tracker.get(value);
        tracker.put(value, thisCount + 1);
      } else {
        tracker.put(value, 0);
      }
    }
    int max = 0;
    for (String key : tracker.keySet()) {
      int count = tracker.get(key);
      if (count > max) {
        majorityClass = key;
        max = count;
      }
    }
    return majorityClass;
  }
      
  /*  
    Returns a subset of data, in which the value of the specfied attribute 
    of all data points is the specified value.  
  */
  public ArrayList<Instance> getSubset(ArrayList<Instance> data, 
                                       int attribute, 
                                       String value) {
    ArrayList<Instance> subset = new ArrayList<Instance>();

    int num = data.size();
    Iterator<Instance> iter = data.iterator();
    while(iter.hasNext()) {
      Instance point = iter.next();
      if (point.attributes[attribute].equals(value)) {
        subset.add(point);
      }
    }
    return subset;
  }

  /*  
    Calculates the entropy of the set of instances
    Right now this function runs, but it's just giving a random number.
    As you may suspect, this will not lead to a nice tree.
    You'll need to figure out how to calculate entropy from 
    the instances passed into the function.

    Some tips: 
      To access instance n : Instance foo = (Instance) data.get(n)
      To get the value for attribute x of instance n : n.attributes[x]
      The first attribute in our data set (attribute 0) is the class attribute,
        It's stored in a hard-coded variable called attributeClass.
      All the attribute values are stored in the attributes variable. To get the 
        3rd value of the 1st attribute: attributes.get(0).get(2).  
        The 0 is getting the first attribute, the 2 is getting the third value 
        of that attribute.
  */
  public double logOfBase(int base, double num) {
	    return Math.log(num) / Math.log(base);
	}

  public double calculateEntropy(ArrayList<Instance> data) {
	  // Don't calculate entropy if there are no data.
		double ent =0;
	    double numData = data.size();
	    if (numData == 0) {
	      return 0;
	   //Entropy! So excite!
	    }else{
	    	 ArrayList<Instance> allE = getSubset(data, 0, "e");
	    	double p=allE.size()/numData;
//	    	System.out.println("alle is "+ allE.size());
//	    	System.out.println("numData "+ numData);
	    	
	    	ArrayList<Instance> allP = getSubset(data, 0, "p");
	    	double p2 = allP.size()/numData;
	    //	System.out.println("p2 is "+ p2);
	    	if(p2==0||p == 0){
	    		return 0;
	    	}else{
	    	ent = p*logOfBase(2,1/p)+p2*logOfBase(2,1/p2);
	    	//System.out.println("ent is "+ ent);
	    	return ent;
	    	
	    }//else statement to calculate entropy
	  }//calculateEntropy
  }
  /*  
    This function splits the specified node according to the id3 
    algorithm.  It recursively divides all children nodes until it is 
    not possible to divide any further.
  */
  public void splitNode(Node node, ArrayList<Integer> attributeList) {

    // Don't split if there are no attributes to split on.
    if (attributeList.size() == 0) {
      return;
    }

    double bestEntropy=0;
    boolean selected=false;
    int selectedAttribute=0;

    node.entropy = calculateEntropy(node.data);
    if (node.entropy == 0) {
      return;
    }

    // Find the maximum decrease in entropy.
    // Loop over all the different attributes, skipping the class attribute.
    for (int i=0; i<attributeList.size(); i++) {
      int thisAttribute = attributeList.get(i);
      if (attributeClass == thisAttribute ) {
        continue;
      }
      // How many values does this attribute have?
      int numValues = attributes.get(thisAttribute).size();
      // Loop over all the values of this attribute.
      double runningEntropy = 0;
      for (int j=0; j<numValues; j++) {
     	  double ent =0;
     	 String valueJ =attributes.get(thisAttribute).get(j);     	 
    	  ArrayList<Instance>sub = getSubset(node.data,thisAttribute,valueJ);  // Use the getSubset function to find the instances in your 
          // data that have value j on attribute i.
    	  if (sub.size()==0){  // Once you have the subset, make sure that there are actually
    	        // elements in that subset.  If not, skip to the next value.
    		  continue;
    	  }else{
    		ent = calculateEntropy(sub); // Calculate the entropy of this subset.
    		double weighted = ent*sub.size();
    		runningEntropy = runningEntropy +weighted; // And add the weighted sum of the entropy to your runningEntropy. 
    		// Basically multiply the entropy by the number of things in your subset.
    	  }
      }

      // Compute the average.
      runningEntropy = runningEntropy / node.data.size(); // Weighted average.
      if (selected == false) {
        selected = true;
        bestEntropy = runningEntropy;
        selectedAttribute = thisAttribute;
      } else {
        if (runningEntropy < bestEntropy) {
          bestEntropy = runningEntropy;
          selectedAttribute = thisAttribute;
        }
      }
    }

    if (selected == false) {
      return;
    }

    // Now divide the dataset using the selected attribute.
    int numValues = attributes.get(selectedAttribute).size();
    node.splitAttribute = selectedAttribute;
    node.children = new Node[numValues];
    for (int j = 0; j < numValues; j++) {
      node.children[j] = new Node();
      node.children[j].parent = node;
      String thisValue = attributes.get(selectedAttribute).get(j);
      node.children[j].data = 
        getSubset(node.data, selectedAttribute, thisValue);
      node.children[j].splitValue = thisValue;
    }

    // Recursively divide children nodes.
    // First, remove the attribute from the attribute list.
    attributeList.remove(new Integer(selectedAttribute));
    for (int j = 0; j < numValues; j++) {
      splitNode(node.children[j], attributeList);
    }
  }

  /* Classify a test instance.  attributes is the instance 
   * to test, node is the node in the tree we're considering.
  */
  public String classify(String[] attributes, Node node){
	  String result = "";
	  int outputAttribute = attributeClass;
	  int whereItsAtt = node.splitAttribute; 
	  while (node.children!=null){
		  for(int i = 0;i < node.children.length;i++){
			   String splitValU=node.children[i].splitValue;
			 	  if (splitValU.equals(attributes[whereItsAtt])){
			 		 node = node.children[i];
			 		 if(node.children==null){
			 			 break;
			 		 }
			 	  }
		  }
	  }
	  ArrayList<String> values = getAllValues(node.data, outputAttribute );
	  if (values.size()==1){		 
		  result =values.get(0)+"";
		 // System.out.println(values.get(0)+"");
	  }else{
		  result =""+majorityClass(node.data);
	  }
	  return result;
  }
    

  
  public String classify2(String[] attributes, Node node) {
	  String result = "";
	  int outputAttribute = attributeClass;

	  // If we're at a leaf node (it'll have no children).
	  if (node.children == null) {
		  ArrayList<String> values = getAllValues(node.data, outputAttribute );
		  if (values.size()==1){		 
			  result =values.get(0)+"";
			 // System.out.println(values.get(0)+"");
		  }else{
			  result =""+majorityClass(node.data);
		  }
	  } else {// Otherwise look at the node's splitAttribute to
		  // see what attribute this node is splitting on, and check the
		  // splitValue of the node's children to see what their values are
		  // and then recurse, calling classify on the correct child. 
		  
		  int whereItsAtt = node.splitAttribute; 
		
		  for(int i = 0;i < node.children.length;i++){
			   String splitValU=node.children[i].splitValue;
			 	  if (splitValU.equals(attributes[whereItsAtt])){
			 		 result = classify2(attributes,node.children[i]);
			 	  }
			 	  //
		  }
	  }
	  return result;
  }

  
/* Read in the test instances and classify each */
public int test(String filename) throws Exception {

    // Open the file.
    FileInputStream in = null;
    try {
      File inputFile = new File(filename);
      in = new FileInputStream(inputFile);
    } catch ( Exception e) {
      System.err.println( "Unable to open data file: " + 
        filename + "\n" + e);
      return 0;
    }

    // Count number of attributes.
    BufferedReader bin = new BufferedReader(new InputStreamReader(in));
    String input = bin.readLine();
    if (input == null) {
      System.err.println( "No data found in the data file: " + 
          filename + "\n");
      return 0;
    }

    StringTokenizer tokenizer = new StringTokenizer(input);
    int numAttributes = tokenizer.countTokens();
    int instanceCount = 0;
    int count =0;

    // For each thing in the test file.
    while(true) {
      instanceCount++;
      input = bin.readLine();
      if (input == null) {
        break;
      }
        
      String[] attributes = new String[numAttributes]; 

      tokenizer = new StringTokenizer(input);
      int numTokens = tokenizer.countTokens();
      for (int i = 0; i < numTokens; i++) {
        attributes[i] = tokenizer.nextToken();
        
      }
      
      String thisClass = classify(attributes, root);
      if (thisClass.equals(attributes[0])){
    	  count++;
      }
      //System.out.println("Accuracy: "+ count +"/"+instanceCount);
//      System.out.print(instanceCount + ":");
//      System.out.print(" predicted:" + thisClass);
//      System.out.println(" actual:" + attributes[0]);
    
      
    }
    System.out.println("Accuracy: "+ count +"/"+instanceCount);
    return 1;
  }


  /* 
    Function to read the data file.
    The first line of the data file should contain the names of 
    all attributes.  The number of attributes is inferred from the 
    number of words in this line.  The last word is taken as the name of 
    the output attribute.  Each subsequent line contains the values of 
    attributes for a data point.
  */
  public int readData(String filename) throws Exception {

    FileInputStream in = null;
    try {
      File inputFile = new File(filename);
      in = new FileInputStream(inputFile);
    } catch ( Exception e) {
      System.err.println( "Unable to open data file: " + 
        filename + "\n" + e);
      return 0;
    }

    BufferedReader bin = new BufferedReader(new InputStreamReader(in));

    String input;

    // Read the first line;
    input = bin.readLine();
    if (input == null) {
      System.err.println( "No data found in the data file: " + 
          filename + "\n");
      return 0;
    }

    StringTokenizer tokenizer = new StringTokenizer(input);
    numAttributes = tokenizer.countTokens();
    if (numAttributes <= 1) {
      System.err.println("Read line: " + input);
      System.err.println("Could not obtain the names of attributes.");
      System.err.println("Expecting at least one input attribute and " +
        "one output attribute");
      return 0;
    }

    attributes = new ArrayList<ArrayList<String>>();
    for (int i = 0; i < numAttributes; i++) {
      attributes.add(new ArrayList<String>());
    }
    attributeNames = new String[numAttributes];

    for (int i=0; i < numAttributes; i++) {
      attributeNames[i]  = tokenizer.nextToken();
    }

    while(true) {
      input = bin.readLine();
      if (input == null) break;

      tokenizer = new StringTokenizer(input);
      int numtokens = tokenizer.countTokens();
      if (numtokens != numAttributes) {
        System.err.println( "Read " + root.data.size() + " data");
        System.err.println( "Last line read: " + input);
        System.err.println( "Expecting " + numAttributes  + " attributes");
        return 0;
      }

      Instance point = new Instance(numAttributes);
      String value;
      for (int i = 0; i < numAttributes; i++) {
        value = tokenizer.nextToken(); 
        point.attributes[i] = value;
        int index = attributes.get(i).indexOf(value);
        if (index < 0) {
          attributes.get(i).add(value);
        }
      }
      root.data.add(point);

    }
    bin.close();
    return 1;

  }

  /*  
    This function prints the decision tree in the form of rules.
    The action part of the rule is of the form 
      outputAttribute = "symbolicValue"
  */

  public void printTree(Node node, String tab) {
    int outputAttribute = attributeClass;
    
    // If we're at a leaf print out the class.
    if (node.children == null) {
      ArrayList<String> values = getAllValues(node.data, outputAttribute );

      // If we know the class then print it, otherwise, print the majority
      // of the parent.
      if (values.size() == 1) {
        System.out.println(tab + "\t" + attributeNames[outputAttribute] + 
          " = \"" + values.get(0) + "\";");
      } else {
        System.out.print(tab + "\t" + attributeNames[outputAttribute] + " = {");
        System.out.print(majorityClass(node.parent.data));
        System.out.println( " };");
      }
      return;
    }

    // If we're not at a leaf, call printTree on each child.
    int numValues = node.children.length;

    for (int i=0; i < numValues; i++) {
      System.out.println(tab + "if( " + 
        attributeNames[node.splitAttribute] + " == \"" +
        attributes.get(node.splitAttribute).get(i) + "\") {" );
        printTree(node.children[i], tab + "\t");
        if (i != numValues - 1 ) {
          System.out.print(tab +  "} else ");
        } else {
           System.out.println(tab +  "}");
        }
     }
   }

    /*  
      This function creates the decision tree and prints it in the 
      form of rules on the console.
    */
    public void createDecisionTree() {
            ArrayList<Integer> splitAttributes = new ArrayList<Integer>();
            for(int i = 0; i < numAttributes; i++) {
              splitAttributes.add(i);
            }
            splitNode(root, splitAttributes);
            printTree(root, "");
    }


    // Here is the definition of the main function. 
    public static void main(String[] args) throws Exception {

      ID3Skeleton myID3 = new ID3Skeleton();
      Scanner in = new Scanner(System.in);

      System.out.print("Input file: ");
      String str = in.nextLine();

      int status = myID3.readData(str);
      if (status <= 0) {
          return;
      }
      myID3.createDecisionTree();
      System.out.print("Test file: ");
      str = in.nextLine();
      myID3.test(str);
   }
}
