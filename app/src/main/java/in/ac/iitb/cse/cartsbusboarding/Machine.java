package in.ac.iitb.cse.cartsbusboarding;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;

import javax.crypto.Mac;

import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;
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
    final String _Classname = this.getClass().getSimpleName();
    AccEngine mAccEngine;
    Context mContext;


    public Machine(AccEngine accEngine){
        mAccEngine = accEngine;
        mContext = accEngine.getContext();
    }
    /**
     * Checks if staircase pattern found in acc buffer data
     *
     * @return true if detected at least one staircase pattern
     */
    public boolean foundStairPattern() {
        double[] idx = testMachine();
        //Match if all values are equal
        double old = idx[0];
        for(double idxVal : idx) {
            if (idxVal != old)
                return true;
        }
        return false;
    }

    private svm_model trainMachine() {
        svm_parameter parameter = new svm_parameter();
        parameter.kernel_type = svm_parameter.LINEAR;

        //TODO: pass train filename
        try {
            InputStream istream = mContext.getAssets().open("SPECTF_expert.train");
            MyReadData featureData = readData(new BufferedReader(new InputStreamReader(istream)));


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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getTestData() {
        FeatureCalculator mFeatureCalculator = new FeatureCalculator(mAccEngine);
        double[] features = new double[]{
                mFeatureCalculator.getMean(),
                mFeatureCalculator.getStd(),
                mFeatureCalculator.getDCComponent(),
                mFeatureCalculator.getEnergy()
        };


        String output_strings = "";
        output_strings += "1 ";     //To make it look like train file
        int index = 1;
        for(double feature_value : features){
            output_strings += index+":"+feature_value+" ";
            index++;
        }
        //XXX: This is just one line!
        return output_strings;
    }

    /**
     * Train and test the machine with mainBuffer data
     * @return idx values predicted
     */
    double[] testMachine() {
        svm_model model = trainMachine();

//        TODO:Create test file from buffered data
        MyReadData data = readData(new BufferedReader(new StringReader(getTestData())));

        int dataSize = data.featuresData.size();
        double[] idx = new double[dataSize];
        for (int i=0; i< dataSize; ++i) {
//            HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
            HashMap<Integer, Double> tmp = data.featuresData.get(i);
            int numFeatures = tmp.keySet().size();
            svm_node[] x = new svm_node[numFeatures];
            int featureIndx = 0;
            for (Integer feature : tmp.keySet()) {
                x[featureIndx] = new svm_node();
                x[featureIndx].index = feature;
                x[featureIndx].value = tmp.get(feature);
                featureIndx++;
            }
            idx[i] = svm_predict(model, x);
        }

        Log.d(_Classname, "Predection: " + idx);
        return idx;
    }

    private MyReadData readData(BufferedReader reader) {
        /** TODO */

        HashSet<Integer> features = new HashSet<Integer>();
        MyReadData data = new MyReadData();


        try {
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
