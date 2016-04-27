package mess.Features;

import weka.core.Instances;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public abstract class Features {
    protected Instances m_allFeat = null;

    public Instances toWeka() {
        return this.m_allFeat;
    }

}
