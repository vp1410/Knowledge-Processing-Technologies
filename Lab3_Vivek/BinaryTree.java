
import java.util.*;

/**
 * 
 * @author Vivek Panchal
 * 04/14/19
 * a node in a binary search tree
 */
class BTNode{
	BTNode left, right;
	String term;
	ArrayList<Integer> docLists;
	
	/**
	 * Create a tree node using a term and a document list
	 * @param term the term in the node
	 * @param docList the ids of the documents that contain the term
	 */
	public BTNode(String term, ArrayList<Integer> docList)
	{
		this.term = term;
		this.docLists = docList;
	}
	
}

/**
 * 
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTree {

	/**
	 * insert a node to a subtree 
	 * @param node root node of a subtree
	 * @param iNode the node to be inserted into the subtree
	 */
	public void add(BTNode node, BTNode iNode)
	{
	  //TO BE COMPLETED
            BTNode head = node;
	    while (head!=null){
            if(head.term.compareTo(iNode.term) > 0){
                if(head.left == null) {
                    head.left = iNode;
                    return;
                }else {
                     System.out.println(" Inserted " + iNode.term +
                        " to left node " + node.term);
                    head = head.left;
                }
            }else {
                if(head.right == null){
                    head.right = iNode;
                    return;
                }else {
                    System.out.println(" Inserted " + iNode.term +
                        " to right of node " + node.term);
                    head = head.right;
                }
            }
        }
	}
	
	/**
	 * Search a term in a subtree
	 * @param n root node of a subtree
	 * @param key a query term
	 * @return tree nodes with term that match the query term or null if no match
	 */
	public BTNode search(BTNode n, String key)
	{
            //TO BE COMPLETED
          BTNode result = null;
        if(n == null)
		    return null;
        if(n.term.compareTo(key) == 0){
            return n;
        }
        if(n.term.compareTo(key) > 0) {
            result = search(n.left,key);
        }
        if(n.term.compareTo(key) < 0)
            result = search(n.right,key);
        return result;
	}
	
	/**
	 * Do a wildcard search in a subtree
	 * @param n the root node of a subtree
	 * @param key a wild card term, e.g., ho (terms like home will be returned)
	 * @return tree nodes that match the wild card
	 */
	public ArrayList<BTNode> wildCardSearch(BTNode n, String key)
	{
		//TO BE COMPLETED
          BTNode rootWildCardNode = _widlCardRootNode(n,key);//gets the root of sub tree that has wildcard
          ArrayList<BTNode> wildCardSearches = new ArrayList<>();
          _wildCardInOrder(rootWildCardNode,key, wildCardSearches); // recursively traverse subtree that has wild cards
          return wildCardSearches; // returns the final nodes that were found during subtree traversal
	}
        
      /**
     * In order traversal of subtree that contains wild card
     * and return all the nodes that contains wild card
     * @param wildCardRoot root of the subtree
     * @param key wildcard
     * @param nodes nodes that contain wild card
     */
    private void _wildCardInOrder(BTNode wildCardRoot, String key, List<BTNode> nodes){
	 if(wildCardRoot!=null){
	      _wildCardInOrder(wildCardRoot.left, key, nodes);
	       if(wildCardRoot.term.contains(key)){
	            nodes.add(wildCardRoot);
            }
            _wildCardInOrder(wildCardRoot.right,key,nodes);
        }
    }
     /**
     * Similar to search function but instead of returning a single node
     * it returns the root of subtree that contains wildcards
     * @param n root of actual tree
     * @param key wild card
     * @return root node of subtree
     */
	private BTNode _widlCardRootNode(BTNode n, String key){
        BTNode result = null;
        if(n == null)
            return null;
        if(n.term.contains(key)){
            return n;
        }
        if(n.term.compareTo(key) > 0) {
            result = _widlCardRootNode(n.left,key);
        }
        if(n.term.compareTo(key) < 0)
            result = _widlCardRootNode(n.right,key);
        return result;
    }
        
	
	/**
	 * Print the inverted index based on the increasing order of the terms in a subtree
	 * @param node the root node of the subtree
	 */
	public void printInOrder(BTNode node)
	{
	//TO BE COMPLETED
            if(node!=null){
            printInOrder(node.left);
            System.out.println(node.term);
            printInOrder(node.right);
	    }
	}
}


