import java.util.ArrayList;
/**
 * ISTE-612-2185 Lab #2
 * Vivek Panchal
 * 03/04/2019
 */

public class PositionalIndex {
    String[] myDocs;
    ArrayList<String> termDictionary;
    ArrayList<ArrayList<DocId>> docLists;

    /**
     * Construct a positional index
     * @param docs List of input strings or file names
     *
     */
    public PositionalIndex(String[] docs)
    {
        //TASK1: TO BE COMPLETED
        myDocs = docs;
        termDictionary = new ArrayList<String>();
        docLists = new ArrayList<ArrayList<DocId>>();
        ArrayList<DocId> docList;
        for(int i=0;i<myDocs.length;i++){
            String[] tokens = myDocs[i].split(" ");
            String token;
            for(int j=0;j<tokens.length;j++){
                token = tokens[j];
                if(!termDictionary.contains(token)){
                    termDictionary.add(token);
                    docList = new ArrayList<DocId>();
                    DocId doid = new DocId(i,j);
                    docList.add(doid);
                    docLists.add(docList);
                }
                else{ //if it is an existing term
                    int index = termDictionary.indexOf(token);
                    docList = docLists.get(index);
                    int k=0;
                    boolean match = false;
                    //search the postings for a doc id, if there is a match, insert a new position
                    //number to the document id
                    for(DocId doid:docList)
                    {
                        if(doid.docId==i)
                        {
                            doid.insertPosition(j);
                            docList.set(k, doid);
                            match = true;
                            break;
                        }
                        k++;
                    }
                    //if no match is found , add a new doc id along with the position number
                    if(!match)
                    {
                        DocId doid = new DocId(i,j);
                        docList.add(doid);
                    }
                }
            }
        }
    }

    /**
     * Return the string representation of a positional index
     */
    public String toString()
    {
        String matrixString = new String();
        ArrayList<DocId> docList;
        for(int i=0;i<termDictionary.size();i++){
            matrixString += String.format("%-15s", termDictionary.get(i));
            docList = docLists.get(i);
            for(int j=0;j<docList.size();j++)
            {
                matrixString += docList.get(j)+ "\t";
            }
            matrixString += "\n";
        }
        return matrixString;
    }

    /**
     *
     * @param qAL1 first postings
     * @param qAL2 second postings
     * @return merged result of two postings
     */
    //need to change the parameters accepted by the intersect method so as get used in the phraseQuery.(Compared to the original method)
    public ArrayList<DocId> intersect(ArrayList<DocId> qAL1, ArrayList<DocId> qAL2)
    {
        //TASK2: TO BE COMPLETED
        ArrayList<DocId> intersectList = new ArrayList<DocId>();
        int pAL1=0,pAL2=0;
        while(pAL1 < qAL1.size() && pAL2 < qAL2.size()) {
            if(qAL1.get(pAL1).docId == qAL2.get(pAL2).docId) {
                ArrayList<Integer> posAL1 = qAL1.get(pAL1).positionList;
                ArrayList<Integer> posAL2 = qAL2.get(pAL2).positionList;

                int pposAL1=0, pposAL2=0;

                while(pposAL1 < posAL1.size()) {
                    while(pposAL2 < posAL2.size()) {
                        if(posAL1.get(pposAL1) - posAL2.get(pposAL2) == -1) {
                            intersectList.add(qAL2.get(pAL2));
                            break;
                        }
                        pposAL2++;
                    }
                    pposAL1++;
                }
                pAL1++;
                pAL2++;
            }
            else if(qAL1.get(pAL1).docId < qAL2.get(pAL2).docId) pAL1++;
            else pAL2++;
        }
        return intersectList;
    }

    /**
     *
     * @param query a phrase query that consists of any number of terms in the sequential order
     * @return ids of documents that contain the phrase
     */
    public ArrayList<DocId> phraseQuery(String[] query)
    {
        //TASK3: TO BE COMPLETED
        ArrayList<DocId> mergedList = new ArrayList<DocId>();
        mergedList = docLists.get(termDictionary.indexOf(query[0]));
        for (int i = 1 ; i < query.length ; i++){
            ArrayList<DocId> tempList = docLists.get(termDictionary.indexOf(query[i]));
            mergedList = intersect(mergedList, tempList);
        }
        return mergedList;
    }


    public static void main(String[] args)
    {
        String[] docs = {"data text warehousing over big data",
                "dimensional data warehouse over big data",
                "nlp before text mining",
                "nlp before text classification"};
        PositionalIndex pi = new PositionalIndex(docs);
        System.out.println("The Positional Index matrix is as follows:\n");
        System.out.print(pi);
        //TASK4: TO BE COMPLETED: design and test phrase queries with 2-5 terms

        System.out.println("**********");
        //query 1: for 2 phrase query
        System.out.println("Query1: nlp,before");
        String[] query1 = {"nlp", "before"};
        ArrayList<DocId> result = pi.phraseQuery(query1);
        if(result!=null)
        {
            for(int i = 0 ; i < result.size() ; i++){
                System.out.println(docs[result.get(i).docId]);
            }

        }
        else
            System.out.println("There are no matches!");

        //query 2: for 3 phrase query
        System.out.println("**********");
        System.out.println("Query2: over,big,data");
        String[] query2 = {"over", "big", "data"};
        ArrayList<DocId> result2 = pi.phraseQuery(query2);
        if(result!=null)
        {
            for(int i = 0 ; i < result2.size() ; i++){
                System.out.println(docs[result2.get(i).docId]);
            }

        }
        else
            System.out.println("There are no matches!");

        //query 3: for 4 phrase query
        System.out.println("**********");
        System.out.println("Query3: nlp,before,text,mining");
        String[] query3 = {"nlp", "before", "text", "mining"};
        ArrayList<DocId> result3 = pi.phraseQuery(query3);
        if(result!=null)
        {
            for(int i = 0 ; i < result3.size() ; i++){
                System.out.println(docs[result3.get(i).docId]);
            }

        }
        else
            System.out.println("There are no matches!");

        //query 4: for 5 phrase query
        System.out.println("**********");
        System.out.println("Query4:dimensional, data, warehouse, over, big");
        String[] query4 = {"dimensional", "data", "warehouse", "over", "big"};
        ArrayList<DocId> result4 = pi.phraseQuery(query4);
        if(result!=null)
        {
            for(int i = 0 ; i < result4.size() ; i++){
                System.out.println(docs[result4.get(i).docId]);
            }

        }
        else
            System.out.println("There are no matches!");
    }
}

/**
 *
 * Document class that contains the document id and the position list
 */
class DocId{
    int docId;
    ArrayList<Integer> positionList;
    public DocId(int did)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
    }
    public DocId(int did, int position)
    {
        docId = did;
        positionList = new ArrayList<Integer>();
        positionList.add(new Integer(position));
    }
    public DocId(int docId1, ArrayList<Integer> pp1) {
        docId = docId1;
        positionList = pp1;
    }

    public void insertPosition(int position)
    {
        positionList.add(new Integer(position));
    }

    public String toString()
    {
        String docIdString = ""+docId + ":<";
        for(Integer pos:positionList)
            docIdString += pos + ",";
        docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
        return docIdString;
    }
}
