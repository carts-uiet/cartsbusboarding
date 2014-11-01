package in.ac.iitb.cse.cartsbusboarding.test.acc;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import in.ac.iitb.cse.cartsbusboarding.MainActivity;
import in.ac.iitb.cse.cartsbusboarding.R;
import in.ac.iitb.cse.cartsbusboarding.acc.AccData;
import in.ac.iitb.cse.cartsbusboarding.acc.AccEngine;
import in.ac.iitb.cse.cartsbusboarding.acc.FeatureCalculator;

/**
 * FeatureCalculator class tests
 */
public class FeatureCalculatorTest extends ActivityUnitTestCase<MainActivity> {
    AccEngine accEngine;
    FeatureCalculator featureCalculator;
    Context mContext;

    public FeatureCalculatorTest() {
        super(MainActivity.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme);
        setActivityContext(context);
        //As this test runs in an IsolatedContext, the test must start the activity,
        //i.e., it is not auto-started by the Android system.
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                MainActivity.class);
        startActivity(intent, null, null);

        mContext = this.getActivity().getApplicationContext();
        accEngine = new AccEngine(mContext);
        featureCalculator = new FeatureCalculator(accEngine);
    }


    /**
     * Set the private currentBuffer to some simple value in featureCalculator
     *
     * @return the value which was just set to currentBuffer
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private Queue<AccData> setBuffer() throws NoSuchFieldException, IllegalAccessException {
        Queue<AccData> newValue = new LinkedList<AccData>();
        AccData temp;

        temp = new AccData(1,1,1);
        newValue.add(temp);

        temp = new AccData(2,2,2);
        newValue.add(temp);

        temp = new AccData(3,3,3);
        newValue.add(temp);

        //featureCalculator = new FeatureCalculator(accEngine);
        Field field = FeatureCalculator.class.getDeclaredField("currentBuffer");
        field.setAccessible(true);
        field.set(featureCalculator, newValue);
        return newValue;
    }

    @SmallTest
    public void testGetMean() throws Exception {
        Queue<AccData> newValue = setBuffer();
        double mean = featureCalculator.getMean();
        assertEquals(3.4641016151377544, mean);
    }


    @SmallTest
    public void testGetStd() throws Exception {
        Queue<AccData> newValue = setBuffer();
        double std = featureCalculator.getStd();
        assertEquals(1.7320508075688774, std);
    }

    @SmallTest
    public void testGetDCComponent() throws Exception {
        Queue<AccData> newValue = setBuffer();
        double dcComp = featureCalculator.getDCComponent();
        assertEquals(5.196152422706632 , dcComp);
    }

    @SmallTest
    public void testGetEnergy() throws Exception {
        Queue<AccData> newValue = setBuffer();
        double energy = featureCalculator.getEnergy();
        assertEquals(2.9999999999999996, energy);
    }

/*
 * Test Private Functions
 * Don't ask why? We just like to live dangerously !
 */

    /**
     * Tests private function "calculateMean"
     */
    public void estFeatureMean() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        featureCalculator = new FeatureCalculator(accEngine);
        Method calculateMean = FeatureCalculator.class.getDeclaredMethod("calculateMean", double[].class);
        calculateMean.setAccessible(true);

        double[] testData = {1, 2, 3};
        Object output = calculateMean.invoke(featureCalculator, testData);
        assertEquals((double) 2, output);
    }

    /**
     * Tests private function "calculateStd"
     */
    public void estFeatureStd() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        featureCalculator = new FeatureCalculator(accEngine);
        Method calculateStd = FeatureCalculator.class.getDeclaredMethod("calculateStd", double[].class);
        calculateStd.setAccessible(true);

        double[] testData = {1, 2, 3};
        Object output = calculateStd.invoke(featureCalculator, testData);
        assertEquals((double) 1, output);
    }
}
