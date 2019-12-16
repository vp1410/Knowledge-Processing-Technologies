/**
 * Name: Vivek Panchal
 * Course: 612 - Knowledge Processing Technologies
 * Lab: #1
 * Date: 02/18/2019
 */

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
/**
 * This class performs Inverted Index Construction and Query Processing.
 */
public class InvertedIndex {

    HashSet<String> stopwordsList;
    ArrayList<String> termList;
    ArrayList<ArrayList<Integer>> docLists;
    String[] files;
    Stemmer stemmer;


    /**
     * Testing the class by creating an object and querying it
     * The result is a container of ArrayList<Integer>
     * @param args  Command-line parameters
     */

    public static void main(String[] args) throws IOException{
        try {
            InvertedIndex iindx = new InvertedIndex();
            ArrayList<Integer> result;

            //TEST FOR SINGLE WORD
            result = iindx.searchquery("acting");
            System.out.println("Files containing the word \"acting \" are:");
            for (Integer f : result) {
                System.out.println(iindx.files[f]);
            }
            result = iindx.searchquery("sagemiller");
            System.out.println("Files containing the word \"sagemiller \" are:" );
            for (Integer f : result) {
                System.out.println(iindx.files[f]);
            }

            //TEST FOR AND operation
            result = iindx.searchquery2("church party");
            String[] queries1 = {"church party "};
            System.out.println("Files containing the word \"church AND party \" are:");
            for (Integer f:result){
                System.out.println(iindx.files[f]);
            }
            result = iindx.searchquery2("warner bros");
            String[] queries = {"warner bros "};
            System.out.println("Files containing the word \"warner AND bros\" are:");
            for (Integer f : result) {
                System.out.println(iindx.files[f]);
            }

            //TEST FOR TWO OR MORE OPERATION
            result = iindx.searchManyOperations("blind AND magic AND kingdom");
            String[] queriesmny = {"blind AND magic AND kingdom"};
            System.out.println("Files containing the word \"blind AND magic AND kingdom\" are:");
            for (Integer f : result) {
                System.out.println(iindx.files[f]);
            }

            //TEST FOR OR OPERATION
            result = iindx.searchquery3("church kid");
            String[] queriesor = {"church kid "};
            System.out.println("Files containing the word \"church OR kid \" are:");
            for (Integer f:result){
                System.out.println(iindx.files[f]);
            }

            result = iindx.searchquery3("stan pretty");
            String[] queries2or = {"stan pretty "};
            System.out.println("Files containing the word \"stan OR pretty \" are:");
            for (Integer f:result){
                System.out.println(iindx.files[f]);
            }
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /** This is my default constructor where all the variables are initialized first. First step is to fetch
    * stop words and store in a Hashset.Second step is to readfiles and tokenzie it.*/

   public InvertedIndex() throws IOException {
        stopwordsList = new HashSet<>();
        termList = new ArrayList<>();
        docLists = new ArrayList<>();
        files = new String[]{"cv000_29416.txt", "cv001_19502.txt", "cv002_17424.txt", "cv003_12683.txt", "cv004_12641.txt"};
        stemmer = new Stemmer();
        stopwordsList();
        readWords();
    }

    /** This method reads the stop words from a file and stores it in a HashSet.
    * It will throw IOException if the word doesnt exist in stopword list*/

    public void stopwordsList()throws IOException{

        Scanner sc = new Scanner(Paths.get("stopwords.txt"));
        while(sc.hasNext()){
            stopwordsList.add(sc.next());
        }
        System.out.println(stopwordsList);

    }
    /**
     * This method reads all the files in the "Lab1_Data" directory.
     * It reads each file word by word and pre-processes it before adding it
     * @throws  IOException if the stop input files are corrupted
     * @see     #InvertedIndex()
     */

    public void readWords()throws IOException{
        int docId = 0;
        for(String temp :files) {
            Scanner sc = new Scanner(Paths.get("Lab1_Data/" + temp));
            while(sc.hasNext()){
                String storewrds = sc.next();
                String [] delimit = storewrds.split("[-+*/.,&%$#!()\"?<>:;{}@^_]+");
                for(String delimt: delimit){
                    if(stopwordsList.contains(delimt)) {
                        continue;
                    }
                    stemmer = new Stemmer();
                    stemmer.add(delimt.toCharArray(),delimt.length());
                    stemmer.stem();
                    delimt = stemmer.toString();
                    if(!termList.contains(delimt)){
                        termList.add(delimt);
                        docLists.add(new ArrayList<>());
                    }
                    if(!docLists.get(termList.indexOf(delimt)).contains(docId))
                        docLists.get(termList.indexOf(delimt)).add(docId);
                }
            }
            docId++;
        }
        for(int i=0;i<termList.size();i++)
            System.out.println(termList.get(i)+" "+docLists.get(i));
    }


    /**
     * This method searches for single word and will return all the documents containing the word
     *
     * @param q words to be queried
     * @return The files containing the word
     */

      //Searching single query
    public ArrayList<Integer> searchquery(String q) {
        Stemmer st = new Stemmer();
        st.add(q.toCharArray(), q.length());
        st.stem();
        if(!termList.contains(st.toString()))
            return new ArrayList<>();
        return docLists.get(termList.indexOf(st.toString()));
    }


    /**
     * This method searches for two words seperated by 'AND'. It makes use of intersectList method
     * to perform its functionality
     * @param q words to be queried
     * @return List of documents that contain the words
     */

    // Searching two query terms(AND)
    public ArrayList<Integer> searchquery2(String q) {
        String[] words = q.split(" ");
        ArrayList<Integer> result1 = searchquery(words[0]);
        ArrayList<Integer> result2 = searchquery(words[1]);
        ArrayList<Integer> result = new ArrayList<>();
        result = intersectList(result1, result2);
         return result;
    }



    //Merge algorithm
    public ArrayList<Integer> intersectList(ArrayList<Integer> L1,ArrayList<Integer> L2){
        ArrayList<Integer> intersectList = new ArrayList<Integer>();
        int AL1=0,AL2=0;
        while(AL1<L1.size()&&AL2<L2.size()){
            if(L1.get(AL1).intValue()==L2.get(AL2).intValue()){
                intersectList.add(L1.get(AL2));
                AL1++;
                AL2++;
            }
            else if(L1.get(AL1)<L2.get(AL2))
                AL1++;
            else
                AL2++;
        }
        return intersectList;
    }

    //OR OPERATION

    public ArrayList<Integer> searchquery3(String q) {
        String[] words = q.split(" ");
        ArrayList<Integer> result1 = searchquery(words[0]);
        ArrayList<Integer> result2 = searchquery(words[1]);
        //int termId = 1;
        ArrayList<Integer> result = new ArrayList<>();
        result = unionList(result1, result2);
        return result;
    }

    //MERGE OR
    private ArrayList<Integer> unionList(ArrayList<Integer> L1, ArrayList<Integer> L2) {
        ArrayList<Integer> unionList = new ArrayList<Integer>();
        int AL1 = 0, AL2 = 0;
        while (AL1 < L1.size() && AL2 < L2.size()) {
            if (L1.get(AL1).intValue() == L2.get(AL2).intValue()) {
                unionList.add(L1.get(AL1));
                AL1++;
                AL2++;
            }
            else if (L1.get(AL1) < L2.get(AL2)) {
                unionList.add(L1.get(AL1));
                AL1++;
            }
            else {
                unionList.add(L2.get(AL2));
                AL2++;
            }
        }
        //MERGING POSTINGS FROM L1
        if (AL1 < L1.size()) {
            while (AL1 < L1.size()) {
                unionList.add(L1.get(AL1));
                AL1++;
            }
        }
        //MERGING POSTINGS FROM  L2
        if (AL2 < L2.size()) {
            while (AL2 < L2.size()) {
                unionList.add(L2.get(AL2));
                AL2++;
            }
        }
        return unionList;
    }

    //SEARCH MANY OPERATIONS, USING HASHMAP TO STORE EACH (key => value pair)
    public ArrayList<Integer> searchManyOperations(String words) {
        String[] q = words.split(" AND ");
         Map<String, ArrayList<Integer>> results = new HashMap<String, ArrayList<Integer>>();
        for (int i = 0; i < q.length; i++) {
            stemmer = new Stemmer();
            stemmer.add(q[i].toCharArray(),q[i].length());
            stemmer.stem();
            String w = stemmer.toString();
            if(!termList.contains(w))
                    return new ArrayList<>();
            results.put(w, docLists.get(termList.indexOf(w)));
        }
        ArrayList<Integer> result = findMin(results);
        System.out.println("Order:");
        System.out.println(result);
        while (!results.isEmpty()) {
            ArrayList<Integer> tempResult = findMin(results);
            System.out.println(tempResult);
            result = intersectList
                    (tempResult, result);
        }
        return result;
    }

    //method to find the shortest keyword first
    public ArrayList<Integer> findMin(Map<String, ArrayList<Integer>> results) {
        String minKey = new String();
        ArrayList<Integer> minPostingsList = new ArrayList<Integer>();
        int size = Integer.MAX_VALUE;
        for (String key : results.keySet()) {
            if (results.get(key).size() < size) {
                minPostingsList = results.get(key);
                minKey = key;
                size = results.get(key).size();
            }
        }
        System.out.println(minKey);
        results.remove(minKey);
        return minPostingsList;

    }
    /**
     * Stemmer, implementing the Porter Stemming Algorithm
     *
     * The Stemmer class transforms a word into its root form.  The input
     * word can be provided a character at time (by calling add()), or at once
     * by calling one of the various stem(something) methods.
     * @param token The word to be stemmed
     * @return  The stemmed word
     */
    public String stemming(String token){
        stemmer = new Stemmer();
        stemmer.add(token.toCharArray(),token.length());
        stemmer.stem();
        return stemmer.toString();
    }


}





