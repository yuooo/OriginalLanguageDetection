package mess.Features;

import org.junit.Test;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;

/**
 * Created by jessicahoffmann on 27/04/2016.
 */
public class LexicalFeature_T {
    @Test
    public void computeUnigram() throws Exception {
        LexicalFeature feat = new LexicalFeature();



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