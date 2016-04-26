package com.mess.main.Algorithm;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.Random;

/**
 * Created by jessicahoffmann on 25/04/2016.
 */
public class ClassifierOLI {
    public AbstractClassifier m_classifier;

    public ClassifierOLI(AbstractClassifier m_classifier) {
        this.m_classifier = m_classifier;
    }

    public ClassifierOLI(String cls) {
        switch (cls.toLowerCase()) {
            case "rf": m_classifier = new RandomForest();
                break;
            case "lr": m_classifier = new SimpleLogistic();
                break;
            case "nb": m_classifier = new NaiveBayesMultinomial();
                break;
            case "nbtxt": m_classifier = new NaiveBayesMultinomialText();
                break;
            default: m_classifier = new SimpleLogistic();
                break;
        }
    }

    public void train(Instances data) throws Exception {
        try {
            this.m_classifier.buildClassifier(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double trainCV(Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(m_classifier, data, 10, new Random(1));
        return eval.correct();
    }
}
