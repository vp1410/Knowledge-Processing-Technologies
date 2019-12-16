import java.util.*;

public class BTreeIndex {
	String[] myDocs;
	BinaryTree termList;
	BTNode root;
	Map<String, Set<Integer>> uniqueDocs;
	/**
	 * Construct binary search tree to store the term dictionary 
	 * @param docs List of input strings
	 * 
	 */
	public BTreeIndex(String[] docs)
	{
		//TO BE COMPLETED
            termList = new BinaryTree();
	    uniqueDocs = new HashMap<>();
	    int docIndex=0;
	    for (String doc : docs){
	        String[] terms = doc.split(" ");
            for (String term : terms) {
                if(uniqueDocs.containsKey(term))
                    uniqueDocs.get(term).add(docIndex);
                else
                    uniqueDocs.put(term, new HashSet<>(Arrays.asList(docIndex)));
            }
            docIndex++;
        }
        ArrayList<String> terms = new ArrayList<>(uniqueDocs.keySet());//converting this into an array of size of uniquedocs
	Collections.sort(terms); // sorting terms
	root = addNodes(terms);
        termList.printInOrder(root);

	}
	
        public BTNode addNodes(ArrayList<String> terms){
        if(terms.size() == 0){
            return null;
        }
        if(terms.size() == 1){
            return new BTNode(terms.get(0), new ArrayList<>(uniqueDocs.get(terms.get(0))));
        }
        String rootTerm = terms.get(terms.size()/2); // this is efficient node
	BTNode root = new BTNode(rootTerm, new ArrayList<>(uniqueDocs.get(rootTerm)));
         
         root.left = addNodes(new ArrayList<>(terms.subList(0,terms.size()/2)));
         root.right = addNodes(new ArrayList<>(terms.subList(terms.size()/2+1,terms.size())));
        
         return root;
        }
        
	/**
	 * Single keyword search
	 * @param query the query string
	 * @return doclists that contain the term
	 */
	public ArrayList<Integer> search(String query)
	{
            BTNode node = termList.search(root, query);
		if(node==null)
		   return null;
		     return node.docLists;
	}
	
	/**
	 * conjunctive query search
	 * @param query the set of query terms
	 * @return doclists that contain all the query terms
	 */
	public ArrayList<Integer> search(String[] query)
	{
		ArrayList<Integer> result = search(query[0]);
		int termId = 1;
		while(termId<query.length)
		{
			ArrayList<Integer> result1 = search(query[termId]);
			result = merge(result,result1);
			termId++;
		}		
		return result;
	}
	
	/**
	 * 
	 * @param wildcard the wildcard query, e.g., ho (so that home can be located)
	 * @return a list of ids of documents that contain terms matching the wild card
	 */
	public ArrayList<Integer> wildCardSearch(String wildcard)
	{
		//TO BE COMPLETED
        ArrayList<BTNode> wildcardSearches = termList.wildCardSearch(root, wildcard);
        ArrayList<Integer> docIds = new ArrayList<>();
        wildcardSearches.stream().forEach(btNode -> {docIds.addAll(btNode.docLists);});//lamda function
        return docIds;
	}
	
	
	private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2)
	{
            ArrayList<Integer> mergedList = new ArrayList<Integer>();
		int id1 = 0, id2=0;
		while(id1<l1.size()&&id2<l2.size()){
		    if(l1.get(id1).intValue()==l2.get(id2).intValue()){
			mergedList.add(l1.get(id1));
			  id1++;
			  id2++;
			}
			else if(l1.get(id1)<l2.get(id2))
			  id1++;
			else
			  id2++;
		}
		return mergedList;
	}
	
	
	/**
	 * Test cases
	 * @param args commandline input
	 */
	public static void main(String[] args)
	{
		String[] docs = {"text warehousing over big data",
                       "dimensional data warehouse over big data",
                       "nlp before text mining",
                       "nlp before text classification"};
		//TO BE COMPLETED with testcases
                BTreeIndex bTreeIndex = new BTreeIndex(docs);
         
         /**
         * For one term
         */
        System.out.println("\nQuery 1 : Searching for Single Keyword : mining");
        ArrayList<Integer> result = bTreeIndex.search("mining");
        if (!result.isEmpty()) {
            for (Integer i : result) {
                System.out.println("\nFound in : " + i);
            }
        } else
            System.out.println("No match!");
        
         /**
         *For conjunctive terms
         */
        String[] query2 = {"nlp", "before"};
        System.out.println("\nQuery 2 : Searching for query terms connected using AND : nlp and before");
        ArrayList<Integer> result1 = bTreeIndex.search(query2);
        if (result1 != null && !result1.isEmpty()) {
            for (Integer i : result1) {
                System.out.println("\nFound in : " + i);
            }
        } else
            System.out.println("No match!");
        
         /**
         *For Wild card Query
         */

        String query3 = "war";
        System.out.println("\nQuery 3 : Searching for wildcard "+query3);
        ArrayList<Integer> result3 = bTreeIndex.wildCardSearch(query3);
        if (result3 != null && !result3.isEmpty()) {
            for (Integer i : result3) {
                System.out.println("\nFound in : " + i);
            }
        } else
            System.out.println("No match!");
        
	}
}