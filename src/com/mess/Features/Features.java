package com.mess.Features;

import weka.core.Instances;

/**
 * Created by jessicahoffmann on 24/04/2016.
 */
public abstract class Features {
    private Instances allFeat = null;

    public Instances toWeka() {
        return this.allFeat;
    };

}
