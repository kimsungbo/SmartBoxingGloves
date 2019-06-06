package com.example.sungbo.databaseexample;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class PunchClassify {

    private static final String WEKA_TEST = "WekaTest";

    private Context mContext;
    private View v;

    private double feature_x;
    private double feature_y;
    private double feature_z;

    private Classifier mClassifier = null;

    TextView mTextViewSamples = null;

    public Sample mSample = new Sample(
            //new Sample(new double[]{4.38,2.78,2.42} // should be straight
            new double[]{1.86,-1.44,4.25} // should be hook
            //new Sample(new double[]{8.09,-5.79,-2.21} // should be uppercut
    );

    private Sample mx;

    public PunchClassify(Context mContext, double feature_x, double feature_y, double feature_z){
        this.mContext = mContext;
        this.feature_x = feature_x;
        this.feature_y = feature_y;
        this.feature_z = feature_z;

        mSample.setFeatures(new double[]{feature_x, feature_y, feature_z});
    }

    public String classifyPunch(){
        if(loadModel() == true){
            String result = classify();

            return result;
        }
        else return "unknown";
    }

    public String classify(){
        String result = "";
        Log.d(WEKA_TEST, "onClickButtonPredict()");

        if(mClassifier==null){
            Toast.makeText(mContext, "Model not loaded!", Toast.LENGTH_SHORT).show();
            return "";
        }

        // we need those for creating new instances later
        // order of attributes/classes needs to be exactly equal to those used for training
        final Attribute x_acceleration = new Attribute("x_acceleration");
        final Attribute y_acceleration = new Attribute("y_acceleration");
        final Attribute z_acceleration = new Attribute("z_acceleration");
        final List<String> classes = new ArrayList<String>() {
            {
                add("straight");
                add("hook");
                add("uppercut");

            }
        };

        // Instances(...) requires ArrayList<> instead of List<>...
        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
            {
                add(x_acceleration);
                add(y_acceleration);
                add(z_acceleration);
                Attribute attributeClass = new Attribute("@@class@@", classes);
                add(attributeClass);
            }
        };
        // unpredicted data sets (reference to sample structure for new instances)
        Instances dataUnpredicted = new Instances("TestInstances",
                attributeList, 1);
        // last feature is target variable
        dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

        DenseInstance newInstance = new DenseInstance(dataUnpredicted.numAttributes()) {
            {
                setValue(x_acceleration, mSample.features[0]);
                setValue(y_acceleration, mSample.features[1]);
                setValue(z_acceleration, mSample.features[2]);
            }
        };
        // reference to dataset
        newInstance.setDataset(dataUnpredicted);

        // predict new sample
        try {
            double temp = mClassifier.classifyInstance(newInstance);
            String className = classes.get(new Double(temp).intValue());
            result = classes.get(new Double(temp).intValue());
            String msg = "predicted: " + className;
            Log.d(WEKA_TEST, msg);
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public boolean loadModel(){
        AssetManager assetManager = mContext.getAssets();
        try {
            mClassifier = (Classifier) weka.core.SerializationHelper.read(assetManager.open("righthand.model"));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Toast.makeText(mContext, "Model loaded.", Toast.LENGTH_SHORT).show();
        return true;
    }

    public class Sample {
        //public int nr;
        public double [] features;

        public Sample(double[] _features) {
            //this.nr = _nr;
            this.features = _features; // 3 features x_acceleration, y_acceleration, z_acceleration
        }

        @Override
        public String toString() {
            return "x : " + features[0]
                    +"  y : " + features[1]
                    +"  z : " + features[2];
        }

        public void setFeatures(double[] features) {
            this.features = features;
        }
    }
}
