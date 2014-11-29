package in.ac.iitb.cse.cartsbusboarding;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;

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
    final String _ClassName = this.getClass().getSimpleName();
    AccEngine mAccEngine;
    Context mContext;
    private static svm_model model_instance = null;

    /**
     *
     * @param accEngine needed to get the features
     */
    public Machine(AccEngine accEngine){
        mAccEngine = accEngine;
        mContext = accEngine.getContext();
        if (model_instance == null)
            model_instance = trainMachine();
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

    /**
     * @return the average idx values
     */
    public double getAvgIdx() {
        double[] idx = testMachine();
        double avg = 0;
        int count1=0, count2=0;
        for(double idxVal : idx) {
            if (idxVal == 1.0)  count1++;
            if (idxVal == 2.0)  count2++;
            avg += idxVal;
        }
        avg /= idx.length;
        Log.i(_ClassName, "Avg IDX: "+avg+" with "+count1+" 1s & "+count2+" 2s");
        return avg;
    }

    /**
     * Set the parameter values for training
     * @return
     */
    private svm_parameter getParameters() {
        svm_parameter parameter = new svm_parameter();
        parameter.svm_type = svm_parameter.C_SVC;
        parameter.kernel_type = svm_parameter.RBF; //XXX: Select right kernel type
        parameter.degree = 3;
        parameter.gamma = 0.25;
        parameter.nu = 0.5;
        parameter.cache_size = 100;
        parameter.C = 1;
        parameter.eps = 1e-3;
        parameter.p = 0.1;
        parameter.shrinking = 1;
        parameter.probability = 0;
        parameter.nr_weight = 0;
        parameter.weight_label = null;
        parameter.weight = null;
        return parameter;
    }

    /**
     * train data is prepared from the file passed and trained using svm machine
     * @return svm_model after training the data
     */
    private svm_model trainMachine() {
        svm_parameter parameter = getParameters();

        //TODO: pass train filename
        try {
            InputStream istream = mContext.getAssets().open("train_data_expert.train");
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
                int index = 0;
                for (Integer id : tmp.keySet()) {
                    svm_node node = new svm_node();
                    node.index = id;
                    node.value = tmp.get(id);
//                    Log.e("train index:value",node.index+":"+node.value);
                    prob.x[i][index] = node;
                    index++;
                }
                //Log.e("train idx",""+featureData.label.get(i));
                prob.y[i] = featureData.label.get(i);
            }

            svm_model model = svm_train(prob, parameter);
            Log.wtf(_ClassName, "Model: "+model.toString());
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Prepares test data by applying features on windows of data in buffer
     * @return string of features applied on data in windows
     */
    private String getTestData() {
        FeatureCalculator mFeatureCalculator = new FeatureCalculator(mAccEngine);
        int windowSize = 20;
        double[][] features = new double[][]{
                mFeatureCalculator.getMean(windowSize),
                mFeatureCalculator.getStd(windowSize),
                mFeatureCalculator.getDCComponent(windowSize),
                mFeatureCalculator.getEnergy(windowSize)
        };

        String output_strings = "";
        int noOfReadings = features[0].length;
        for (int colIndex = 0; colIndex < noOfReadings; colIndex++)  {
            output_strings += "1 ";     //To make it look like train file
            for (int rowIndex = 0; rowIndex < features.length; rowIndex++) {
                double feature_value = features[rowIndex][colIndex];
                output_strings += (rowIndex+1)+":"+feature_value+" ";
            }
            Log.d(_ClassName,output_strings);
            output_strings += "\n";
        }
        //XXX: This is just one line!
        return output_strings;
    }

    /**
     * Train and test the machine with mainBuffer data
     * @return idx values predicted
     */
    double[] testMachine() {
        //Train machine should be called only once

        //creating test data from string returned by getTestData
        MyReadData data = readData(new BufferedReader(new StringReader(getTestData())));

        int dataSize = data.featuresData.size();
        double[] idx = new double[dataSize];
        String print_idx = "";
        for (int i=0; i< dataSize; ++i) {
            HashMap<Integer, Double> tmp = data.featuresData.get(i);
            int numFeatures = tmp.keySet().size();
            svm_node[] x = new svm_node[numFeatures];
            int featureIndex = 0;
            for (Integer feature : tmp.keySet()) {
                x[featureIndex] = new svm_node();
                x[featureIndex].index = feature;
//                Log.d(_ClassName+" feature value",""+tmp.get(feature));
                x[featureIndex].value = tmp.get(feature);
                //Log.e("train index:value",x[featureIndex].index+":"+x[featureIndex].value);

                featureIndex++;
            }
            //Log.e("train idx",""+data.label.get(i));

            idx[i] = svm_predict(model_instance, x);
            print_idx += idx[i] + " ";
        }

        Log.i(_ClassName, "Prediction: " + print_idx);
        return idx;
    }

    /**
     * Separates features and label into different Hash Maps(both in class structure MyReadData)
     * @param reader from where data is to be obtained
     * @return separated data as class structure MyReadData
     */
    private MyReadData readData(BufferedReader reader) {
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

    /**
     * Structure to hold features and labels in separate Hash Maps
     */
    class MyReadData {
        HashMap<Integer, HashMap<Integer, Double>> featuresData;
        HashMap<Integer, Integer> label;

        MyReadData() {
            featuresData = new HashMap<Integer, HashMap<Integer, Double>>();
            label = new HashMap<Integer, Integer>();
        }
    }
}
