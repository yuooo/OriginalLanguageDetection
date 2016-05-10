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

        feat1.loadRawTxt("Data/blocks/" + sizeSlice + "train/", true);
        pT("Load");


        LexicalFeature feat2 = new LexicalFeature();
        feat2.loadData("Data/weka/sentences_blocks/500/train.arff", true);
        assertNotNull(feat2.m_data_train);

        Instances inst1 = feat1.m_data_train;
        Instances inst2 = feat2.m_data_train;

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

        feat1.loadRawTxt("Data/blocks/" + sizeSlice + "train/", true);
        feat1.m_data_test = feat1.m_data_train;
        feat1.computeUnigram();


        LexicalFeature feat2 = new LexicalFeature();
        feat2.loadFeatures("Data/weka/unigram_feat/500/unigram.arff", true);
        assertNotNull(feat2.m_allFeat_train);

        Instances inst1 = feat1.m_allFeat_train;
        Instances inst2 = feat2.m_allFeat_train;

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
        LexicalFeature feat = new LexicalFeature();
        feat.loadData("Data/arff/small_dataset.arff", true);
        feat.loadData("Data/arff/small_dataset.arff", false);

    }


    @Test
    public void loadSave() throws Exception {
        long startTime = System.currentTimeMillis();

        LexicalFeature feat = new LexicalFeature();
        feat.loadRawTxt("Data/txt_clean/", true);

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000 );

        assertEquals(feat.m_data_train.getClass(), Instances.class);
        System.out.println("Class index " + feat.m_data_train.classIndex());
        System.out.println("num inst " + feat.m_data_train.numInstances());
        System.out.println("num attr " + feat.m_data_train.numAttributes());

        startTime = System.currentTimeMillis();
        feat.saveData("Data/arff/small_dataset.arff", true);
        endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000 );
    }

    @Test
    public void computeEtymology() throws Exception {
        LexicalFeature feat = new LexicalFeature();
        feat.computeEtymology();

    }
}