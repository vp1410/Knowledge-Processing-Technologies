import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Vivek Panchal
 */
public class NBClassifier {
    String[] trainingDocs;
    String[] testDocs;
    int[] trainingLabels;
    int[] testingLabels;
    int numClasses = 2;
    int[] classCounts; // number of docs per class
    String[] classStrings; // concatenated string for a given class
    int[] classTokenCounts; // total number of tokens per class
    HashMap<String, Double>[] condProb;
    HashSet<String> vocabulary; // for storing entire vocabuary
    BufferedReader bufferedReader;
    ArrayList<File> files = new ArrayList<File>();


    /**
     * Build a Naive Bayes classifier using a training document set
     * @param trainDataFolder the training document folder
     */
    public NBClassifier(String trainDataFolder)
    {
        preprocess(trainDataFolder);
        classCounts = new int[numClasses];
        classStrings = new String[numClasses];
        classTokenCounts = new int[numClasses];
        condProb = new HashMap[numClasses];
        vocabulary = new HashSet<>();
        for (int i = 0; i < numClasses; i++) {
            classStrings[i] = "";
            condProb[i] = new HashMap<String, Double>();
        }
        for (int i = 0; i < trainingLabels.length; i++) {
            classCounts[trainingLabels[i]]++;
            classStrings[trainingLabels[i]] += (trainingDocs[i] + " ");
        }
        for (int i = 0; i < numClasses; i++) {
            String[] tokens = classStrings[i].split("[\" ()_,?:;%&-]+");
            classTokenCounts[i] = tokens.length;

            // collecting the counts
            for (String token : tokens) {
                vocabulary.add(token);
                if (condProb[i].containsKey(token)) {
                    double count = condProb[i].get(token);
                    condProb[i].put(token, count + 1);
                } else {
                    condProb[i].put(token, 1.0);
                }
            }
        }

        // computing the class conditional probability
        for (int i = 0; i < numClasses; i++) {
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();
            while (iterator.hasNext()) {
                Map.Entry<String, Double> entry = iterator.next();
                String token = entry.getKey();
                Double count = entry.getValue();
                count = (count + 1) / (classTokenCounts[i] + vSize);
                condProb[i].put(token, count);
            }
        }


    }

    /**
     * Classify a test doc
     * @param doc test doc
     * @return class label
     */
    public int classify(String doc){
        int label = 0;
        int vSize = vocabulary.size();
        double[] score = new double[numClasses];
        for (int i = 0; i < score.length; i++) {
            score[i] = Math.log(classCounts[i] * 1.0 / trainingDocs.length);
        }
        String[] tokens = doc.split("[\" ()_,?:;%&-]+");
        for (int i = 0; i < numClasses; i++) {
            for (String token : tokens) {
                if (condProb[i].containsKey(token)) {
                    score[i] += Math.log(condProb[i].get(token));
                } else {
                    score[i] += Math.log(1.0 / (classTokenCounts[i] + vSize));
                }
            }
        }
        double maxScore = score[0];
        for (int i = 0; i < score.length; i++) {
            if (score[i] > maxScore) {
                label = i;
            }
        }

        return label;

    }

    /**
     * Load the training documents
     * @param trainDataFolder
     */
    public void preprocess(String trainDataFolder)
    {
        File folder = new File(trainDataFolder);

        //get the total number documents in the training folder
        String trainDataSubFolder = trainDataFolder + "/pos";
        trainingDocs = new String[new File(trainDataSubFolder).list().length *2];
        trainingLabels = new int[new File(trainDataSubFolder).list().length *2];

        //parse through all the sub directories
        File childDir;
        Stack<File> stack = new Stack<File>();
        stack.push(folder);
        int i = 0;
        while (!stack.isEmpty()) {
            childDir = stack.pop();
            if (childDir.isDirectory()) {
                for (File f : childDir.listFiles()) {
                    stack.push(f);
                }
            } else if (childDir.isFile() && i<1800) {
                trainingDocs[i] = childDir.getAbsolutePath();//cant resolve the error here
                i++;
            }
        }
        int numofdoc = 0;
        for (int j=0; j<trainingDocs.length;j++) {
            File file=new File(trainingDocs[j]);
            if (file.isFile()) {
                try {
                    String stringCurrLine="";
                    String allLines="";
                    bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                    while ((stringCurrLine = bufferedReader.readLine()) != null) {
                        allLines += stringCurrLine;
                    }
                    trainingDocs[numofdoc] = allLines;
                    if (file.getAbsolutePath().contains("pos")) {
                        trainingLabels[numofdoc] = 0;
                    } else {
                        trainingLabels[numofdoc] = 1;
                    }
                    numofdoc++;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     *  Classify a set of testing documents and report the accuracy
     * @param testDataFolder fold that contains the testing documents
     * @return classification accuracy
     */
    public double classifyAll(String testDataFolder)
    {
        File folder = new File(testDataFolder);

        //get the total number number of documents in the trainig folder
        String testDataSubFolder = testDataFolder + "/pos";
        testDocs = new String[new File(testDataSubFolder).list().length*2];
        testingLabels = new int[new File(testDataSubFolder).list().length*2];
        //parse through all sub folders
        File childDirectory;
        Stack<File> stack = new Stack<File>();
        stack.push(folder);
        int iterator = 0;
        while (!stack.isEmpty()) {
            childDirectory = stack.pop();
            if (childDirectory.isDirectory()) {
                for (File f : childDirectory.listFiles()) {
                    stack.push(f);
                }
            } else if (childDirectory.isFile()) {
                if(iterator<200){
                    testDocs[iterator] = childDirectory.getAbsolutePath();
                    iterator++;
                }
            }
        }
        int numberOfDocuments = 0;
        for (int j=0; j<testDocs.length;j++) {
            File file=new File(testDocs[j]);
            if (file.isFile()) {
                try {
                    String stringCurrLine="";
                    String allLines="";
                    bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));

                    while ((stringCurrLine = bufferedReader.readLine()) != null) {
                        allLines += stringCurrLine;
                    }
                    testDocs[numberOfDocuments] = allLines;
                    if (file.getAbsolutePath().contains("pos")) {
                        testingLabels[numberOfDocuments] = 0;
                    } else {
                        testingLabels[numberOfDocuments] = 1;
                    }
                    numberOfDocuments++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //for calculating the accuracy of the NB classifier
        double accuracy;
        int correctInstances = 0;
        for (int i = 0; i <testDocs.length; i++) {
            int cl = classify(testDocs[i]);
            if (cl == testingLabels[i]) {
                correctInstances++;
            }
        }
        System.out.println("Correctly classified " + correctInstances + " out of 200");
        accuracy = (correctInstances*1.0)/testDocs.length;
        return accuracy;

    }


    public static void main(String[] args)
    {
        System.out.println("Naive bayes classifier's results are as follows:");
        String trainDataFolder = "/Users/vivek 14/IdeaProjects/Lab04_Vivek/data/train";
        NBClassifier nb = new NBClassifier(trainDataFolder);
        double accuracy = nb.classifyAll("/Users/vivek 14/IdeaProjects/Lab04_Vivek/data/test");
        System.out.println("Accuracy:" + accuracy);

    }
}
