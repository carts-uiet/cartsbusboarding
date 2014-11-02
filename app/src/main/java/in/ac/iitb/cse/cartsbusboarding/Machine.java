package in.ac.iitb.cse.cartsbusboarding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import static libsvm.svm.svm_predict;
import static libsvm.svm.svm_train;

/**
 * Applies machine learning
 */
public class Machine {
    /**
     * Checks if staircase pattern found in acc buffer data
     *
     * @return true if detected at least one staircase pattern
     */
    public boolean foundStairPattern() {
        return false;
    }

    private svm_model trainMachine() {
        svm_parameter parameter = new svm_parameter();
        parameter.kernel_type = svm_parameter.LINEAR;

        //TODO: pass filename
        MyReadData featureData = readData("");

        //Train the SVM model
        svm_problem prob = new svm_problem();
        int numTrainingInstances = featureData.featuresData.keySet().size();
        prob.l = numTrainingInstances;
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][];

        for (int i = 0; i < numTrainingInstances; i++) {
            HashMap<Integer, Double> tmp = featureData.featuresData.get(i);
            prob.x[i] = new svm_node[tmp.keySet().size()];
            int indx = 0;
            for (Integer id : tmp.keySet()) {
                svm_node node = new svm_node();
                node.index = id;
                node.value = tmp.get(id);
                prob.x[i][indx] = node;
                indx++;
            }

            prob.y[i] = featureData.label.get(i);
        }

        svm_model model = svm_train(prob, parameter);
        return model;
    }

    void testMachine() {
        svm_model model = trainMachine();

        MyReadData data = readData("");

        for (Integer testInstance : data.featuresData.keySet()) {
            HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
            int numFeatures = tmp.keySet().size();
            svm_node[] x = new svm_node[numFeatures];
            int featureIndx = 0;
            for (Integer feature : tmp.keySet()) {
                x[featureIndx] = new svm_node();
                x[featureIndx].index = feature;
                x[featureIndx].value = tmp.get(feature);
                featureIndx++;
            }

            double d = svm_predict(model, x);

            System.out.println(testInstance + "\t" + d);
        }

    }

    private MyReadData readData(String filename) {
        /** TODO */

        HashSet<Integer> features = new HashSet<Integer>();
        MyReadData data = new MyReadData();


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("a1a.train"));
            String line = null;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                data.featuresData.put(lineNum, new HashMap<Integer, Double>());
                String[] tokens = line.split("\\s+");
                int labelToken = Integer.parseInt(tokens[0]);
                data.label.put(lineNum, labelToken);
                for (int i = 1; i < tokens.length; i++) {
                    String[] fields = tokens[i].split(":");
                    int featureId = Integer.parseInt(fields[0]);
                    double featureValue = Double.parseDouble(fields[1]);
                    features.add(featureId);
                    data.featuresData.get(lineNum).put(featureId, featureValue);
                }
                lineNum++;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    class MyReadData {
        HashMap<Integer, HashMap<Integer, Double>> featuresData;
        HashMap<Integer, Integer> label;

        MyReadData() {
            featuresData = new HashMap<Integer, HashMap<Integer, Double>>();
            label = new HashMap<Integer, Integer>();
        }
    }
}
