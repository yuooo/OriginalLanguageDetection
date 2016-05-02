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

    protected void safeMerge(Instances inst) {
        if (m_allFeat == null) {
            m_allFeat = inst;
        }
        else {
            Instances.mergeInstances(m_allFeat, inst);
        }
    }

}
