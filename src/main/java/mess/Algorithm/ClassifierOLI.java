package mess.Algorithm;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.util.Random;

/**
 * Created by jessicahoffmann on 25/04/2016.
 */
public class ClassifierOLI {
    public AbstractClassifier m_classifier;

    public boolean m_trained = false;

    public ClassifierOLI(AbstractClassifier m_classifier) {
        this.m_classifier = m_classifier;
    }

    public ClassifierOLI() {
        m_classifier = new Logistic();
    }

    public ClassifierOLI(String cls) {
        switch (cls.toLowerCase()) {
            case "rf": m_classifier = new RandomForest();
                break;
            case "lr": m_classifier = new Logistic();
                break;
            case "nb": m_classifier = new NaiveBayesMultinomial();
                break;
            case "nbtxt": m_classifier = new NaiveBayesMultinomialText();
                break;
            default: m_classifier = new Logistic();
                break;
        }
    }

    public static Instances csvToInstance(String filename) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filename);

        Instances data = source.getDataSet();
        int indexClass = data.numAttributes() - 1;
        if (data.classIndex() == -1)
            data.setClassIndex(indexClass);

        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]=String.valueOf(indexClass + 1);  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(data);

        Instances newData= Filter.useFilter(data, convert);
        return newData;
    }

    public void train(Instances data) throws Exception {
        try {
            this.m_classifier.buildClassifier(data);
            m_trained = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double trainCV(Instances data) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(m_classifier, data, 10, new Random(1));
        return eval.pctCorrect();
    }

    public double test(Instances data, Instances dataTest) throws Exception {
        assert m_trained = true;
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(m_classifier, dataTest);
        return eval.pctCorrect();
    }
}
