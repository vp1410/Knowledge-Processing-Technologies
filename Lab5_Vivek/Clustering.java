
import java.util.*;
import java.lang.Math;

/**
 * 612 Lab 5
 * Document clustering
 * @author  Vivek Panchal
 * Lab      05
 * Class    Knowledge Process Technologies
 */
public class Clustering {
	
	int numDocs;
	int numClusters;
	int vSize;
	Doc[] docList;
	HashMap<String, Integer> termIdMap;
	
	ArrayList<Doc>[] clusters;
	Doc[] centroids;
   
	public Clustering(int numC)
	{
		numClusters = numC;
		clusters = new ArrayList[numClusters];
      for(int i = 0; i < numClusters; i++) {
         clusters[i] = new ArrayList<Doc>();
      }
		centroids = new Doc[numClusters];
		termIdMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Load the documents to build the vector representations
	 * @param docs
	 */
	public void preprocess(String[] docs){
		numDocs = docs.length;
		docList = new Doc[numDocs];
		int termId = 0;
		
		//collect the term counts, build term id map and the idf counts
		int docId = 0;
		for(String doc:docs){
			String[] tokens = doc.split(" ");
			Doc docObj = new Doc(docId);
			for(String token: tokens){
				if(!termIdMap.containsKey(token)){
					termIdMap.put(token, termId);
					docObj.termIds.add(termId);
					docObj.termWeights.add(1.0);					
					termId++;
				}
				else{
					Integer tid = termIdMap.get(token);
					int index = docObj.termIds.indexOf(tid);
					if (index >0){
						double tw = docObj.termWeights.get(index);
						docObj.termWeights.add(index, tw+1);
					}
					else{
						docObj.termIds.add(termIdMap.get(token));
						docObj.termWeights.add(1.0);
					}
				}
			}
			docList[docId] = docObj;
			docId++;
		}
		vSize = termId;
		//System.out.println("vSize: " + vSize);
		
		//compute the tf-idf weights of documents
		for(Doc doc: docList){
			double docLength = 0;
			double[] termVec = new double[vSize];
			for(int i=0;i<doc.termIds.size();i++){
				Integer tid = doc.termIds.get(i);
				double tfidf = (1+Math.log(doc.termWeights.get(i)));//Math.log(numDocs/idfMap.get(tid));				
				doc.termWeights.set(i, tfidf);
				docLength += Math.pow(tfidf, 2);
			}
			
			//normalize the doc vector			
			for(int i=0;i<doc.termIds.size();i++){
				double tw = doc.termWeights.get(i);
				doc.termWeights.set(i, tw/docLength);
				//System.out.println(doc.termIds.get(i));
				termVec[doc.termIds.get(i)] = tw/docLength;
			}
			doc.termVec = termVec;
			//doc.termIds = null;
			//doc.termWeights = null;
		}
	}
	
	/**
	 * Cluster the documents
	 * For kmeans clustering, use the first and the ninth documents as the initial centroids
	 */
	public void cluster(){
		//TO BE COMPLETED
      centroids[0] = docList[0];
      centroids[1] = docList[8];
      Doc[] oldCentroids = new Doc[numClusters];
      
      while(!(stoppingCriteria(oldCentroids, this.centroids))) {

         for(int i = 0; i < numClusters; i++) {
            oldCentroids[i] = this.centroids[i];
         }
         newCluster();
         newCentroids();

         for(int i = 0; i < numClusters; i++) {
            System.out.print("Cluster " + i + ": ");
            for(Doc d: clusters[i]) {
               System.out.print(d.docId + " ");
            }
         }
      }
      
 	}
   

   public void newCluster() {
      double similarity1 = 0, similarity2 = 0;
      for(int i = 0; i < numDocs; i++) {
         similarity1 = cosineSimilarity(docList[i].termVec, centroids[0].termVec);
         similarity2 = cosineSimilarity(docList[i].termVec, centroids[1].termVec);
         if(similarity1 >= similarity2) {
            if (!clusters[0].contains(docList[i])) {
               clusters[1].remove(docList[i]);
               clusters[0].add(docList[i]);
            }
         }
         else {
            if (!clusters[1].contains(docList[i])) {
               clusters[0].remove(docList[i]);
               clusters[1].add(docList[i]);
            }
         }
      }
     /* for(int i = 0; i < numClusters; i++) {
         System.out.print("Cluster " + i + ": ");
         for(Doc d: clusters[i]) {
            System.out.print(d.docId + " ");
         }
         System.out.println();
      }*/
   }
   
   public void newCentroids() {

      for(int i = 0; i < numClusters; i++) {
         Doc newDocument = new Doc();
         double[] newVector = new double[vSize];
         for(int j = 0; j < vSize; j++) {
            double sum = 0;
            for(int k = 0; k < this.clusters[i].size(); k++) {
               sum += this.clusters[i].get(k).termVec[j];
            }
            newVector[j] = sum / this.clusters[i].size() * 1.0;
         }
         newDocument.setTermVec(newVector);
         this.centroids[i] = newDocument;
         
      }
   }
   
   
   /**
    * Calculating the similarity between documents
    */
  
    private double cosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0,magnitude1 = 0.0,magnitude2 = 0.0;
        double cosineSimilarity = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            magnitude1 += Math.pow(vector1[i], 2);
            magnitude2 += Math.pow(vector2[i], 2);
        }
        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);
        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        }
        return cosineSimilarity;
    }
    
    
    public boolean stoppingCriteria(Doc[] old ,Doc[] _new)
    {
        boolean equal =  true;
        if(old[0]== null){
            return false;
        }
      for(int i=0;i<numClusters;i++){
        double[] termVector1 = old[i].termVec;
        double[] termVector2 = _new[i].termVec;
       for(int j= 0;j<termVector1.length;j++){
            if(termVector1[j]!= termVector2[j]){
        equal = false;
}
}
}
      return equal;
    }
	
	
	public static void main(String[] args){
		String[] docs = {"hot chocolate cocoa beans",
				 "cocoa ghana africa",
				 "beans harvest ghana",
				 "cocoa butter",
				 "butter truffles",
				 "sweet chocolate can",
				 "brazil sweet sugar can",
				 "suger can brazil",
				 "sweet cake icing",
				 "cake black forest"
				};
		Clustering c = new Clustering(2);
		c.preprocess(docs);
		System.out.println("Vector space representation:");
		for(int i=0;i<c.docList.length;i++){
			System.out.println(c.docList[i]);
		}
		c.cluster();
	}
}

/**
 * 
 * Document id class that contains the document id and the term weight in tf-idf
 */
class Doc{
	int docId;
	ArrayList<Integer> termIds;
	ArrayList<Double> termWeights;
	double[] termVec;
	public Doc(){
		
	}
	public Doc(int id){
		docId = id;
		termIds = new ArrayList<Integer>();
		termWeights = new ArrayList<Double>();
	}
	public void setTermVec(double[] vec){
		termVec = vec;
	}
   
	public String toString()
	{
		String docString = "[";
		for(int i=0;i<termVec.length;i++){
			docString += termVec[i] + ",";
		}
		return docString+"]";
	}
	
}