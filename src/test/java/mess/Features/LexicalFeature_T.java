package mess.Features;

import org.junit.Test;
import weka.core.Instances;

import java.util.Arrays;

import static mess.utils.Utils.T;
import static mess.utils.Utils.pT;
import static org.junit.Assert.*;

/**
 * Created by jessicahoffmann on 27/04/2016.
 */
public class LexicalFeature_T {
    @Test
    public void computeUnigram() throws Exception {
        LexicalFeature feat = new LexicalFeature();
    }


    @Test
    public void loadData() throws Exception {
        String sizeSlice = "500/";

        // load data
        T();
        System.out.println("Start Loadind.");
        LexicalFeature feat1 = new LexicalFeature();

        feat1.loadRawTxt("Data/blocks/" + sizeSlice + "train/");
        pT("Load");


        LexicalFeature feat2 = new LexicalFeature();
        feat2.loadData("Data/weka/sentences_blocks/500/train.arff");
        assertNotNull(feat2.m_data);

        Instances inst1 = feat1.m_data;
        Instances inst2 = feat2.m_data;

        assertEquals(inst1.numInstances(), inst2.numInstances(), 1);
        assertEquals(inst1.numAttributes(), inst2.numAttributes(), 1);

        System.out.println(Arrays.toString(inst1.firstInstance().toDoubleArray()));
        System.out.println(Arrays.toString(inst2.firstInstance().toDoubleArray()));

        for (int i = 0; i < inst1.numInstances(); i++)
        {
            assertArrayEquals(inst1.instance(i).toDoubleArray(), inst2.instance(i).toDoubleArray(), 1);
//            System.out.println(inst1.instance(i).classValue());
        }

    }

    @Test
    public void loadFeatures() throws Exception {
        String sizeSlice = "500/";

        // load data
        LexicalFeature feat1 = new LexicalFeature();

        feat1.loadRawTxt("Data/blocks/" + sizeSlice + "train/");
        feat1.computeUnigram();


        LexicalFeature feat2 = new LexicalFeature();
        feat2.loadFeatures("Data/weka/unigram_feat/500/unigram.arff");
        assertNotNull(feat2.m_allFeat);

        Instances inst1 = feat1.m_allFeat;
        Instances inst2 = feat2.m_allFeat;

        assertEquals(inst1.numInstances(), inst2.numInstances(), 1);
        assertEquals(inst1.numAttributes(), inst2.numAttributes(), 1);

//        System.out.println(Arrays.toString(inst1.firstInstance().toDoubleArray()));
//        System.out.println(Arrays.toString(inst2.firstInstance().toDoubleArray()));

        for (int i = 0; i < inst1.numInstances(); i++) {
            assertArrayEquals(inst1.instance(i).toDoubleArray(), inst2.instance(i).toDoubleArray(), 1);
//            System.out.println(inst1.instance(i).classValue());
        }
    }

    @Test
    public void computeBigram() throws Exception {

    }

    @Test
    public void computeFunctionWords() throws Exception {

    }


    @Test
    public void loadSave() throws Exception {
        long startTime = System.currentTimeMillis();

        LexicalFeature feat = new LexicalFeature();
        feat.loadRawTxt("Data/txt_clean/");

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000 );

        assertEquals(feat.m_data.getClass(), Instances.class);
        System.out.println("Class index " + feat.m_data.classIndex());
        System.out.println("num inst " + feat.m_data.numInstances());
        System.out.println("num attr " + feat.m_data.numAttributes());

        startTime = System.currentTimeMillis();
        feat.saveData("Data/arff/small_dataset.arff");
        endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000 );
    }
}