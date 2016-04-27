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
        assertEquals(feat.toWeka().getClass(), Instances.class);


    }

    @Test
    public void computeBigram() throws Exception {

    }

    @Test
    public void computeFunctionWords() throws Exception {

    }

}