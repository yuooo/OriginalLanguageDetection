package mess.Algorithm;

import org.junit.Test;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by jessicahoffmann on 26/04/2016.
 */
public class ClassifierOLI_T {
    @Test
    public void load() throws Exception {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
//        System.out.println("Current relative path is: " + s);


        ConverterUtils.DataSource source = new ConverterUtils.DataSource("Data/weka/train_weka.csv");
        assertNotNull(source);

        Instances data = source.getDataSet();
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);

        assertEquals(data.numInstances(), 10000);
        assertEquals(data.classIndex(), 3);


        double[] firstRow = {1,0,0,0};
        double[] firstRealRow = data.firstInstance().toDoubleArray();
        assertArrayEquals(firstRow, firstRealRow, 0.01);

        double[] tenRow = {0,1,1,2};
        double[] tenRealRow = data.instance(9).toDoubleArray();
        assertArrayEquals(tenRow, tenRealRow, 0.01);

        Instances newData = ClassifierOLI.csvToInstance("Data/weka/train_weka.csv");

        for(int i=0; i<4; i=i+1)
        {
            assertTrue(data.attribute(i).isNumeric());
        }

        for(int i=0; i<3; i=i+1)
        {
            assertTrue(newData.attribute(i).isNumeric());
        }
        assertTrue(newData.attribute(3).isNominal());
    }

    @Test
    public void train() throws Exception {
        assertEquals(1,1);

        ClassifierOLI cloli = new ClassifierOLI("");
        assertEquals(cloli.m_classifier.getClass(), Logistic.class);

        //TODO: test switch option

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("Data/weka/train_weka.csv");
        Instances data = source.getDataSet();
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);
        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="4";  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(data);

        data= Filter.useFilter(data, convert);

        cloli.train(data);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(cloli.m_classifier, data, 10, new Random(1));
//        System.out.println(eval.toSummaryString());
        assertEquals(eval.correct(), data.numInstances(), 1);

        eval = new Evaluation(data);
        eval.evaluateModel(cloli.m_classifier, data);
        assertEquals(eval.correct(), data.numInstances(), 1);

        Instances dataTest = ClassifierOLI.csvToInstance("Data/weka/test_weka.csv");
        eval = new Evaluation(data);
        eval.evaluateModel(cloli.m_classifier, dataTest);
        assertEquals(eval.correct(), dataTest.numInstances(), 1);

    }

    @Test
    public void train2() throws Exception {
        assertEquals(1,1);

        ClassifierOLI cloli = new ClassifierOLI("");
        assertEquals(cloli.m_classifier.getClass(), Logistic.class);

        ConverterUtils.DataSource source = new ConverterUtils.DataSource("Data/weka/train_weka2.csv");
        Instances data = source.getDataSet();
        if (data.classIndex() == -1)
            data.setClassIndex(data.numAttributes() - 1);
        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="4";  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(data);

        data= Filter.useFilter(data, convert);

        cloli.train(data);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(cloli.m_classifier, data, 10, new Random(1));
//        System.out.println(eval.toSummaryString());
        assertEquals(eval.correct(), data.numInstances(), data.numInstances()*0.3);
    }

    @Test
    public void trainCV() throws Exception {
        Instances dataTrain = ClassifierOLI.csvToInstance("Data/weka/train_weka.csv");
        Instances dataTest = ClassifierOLI.csvToInstance("Data/weka/test_weka.csv");
        ClassifierOLI cloli = new ClassifierOLI("");

        double acc = cloli.trainCV(dataTrain);
        assertEquals(acc, 100, 1);
    }

    @Test
    public void test() throws Exception {
        Instances dataTrain = ClassifierOLI.csvToInstance("Data/weka/train_weka.csv");
        Instances dataTest = ClassifierOLI.csvToInstance("Data/weka/test_weka.csv");
        ClassifierOLI cloli = new ClassifierOLI("");
        try {
            cloli.test(dataTrain, dataTest);
            fail("Should have been trained");
        } catch (Exception e) {
        }
        cloli.train(dataTrain);
        double acc = cloli.test(dataTrain, dataTest);
        assertEquals(acc, 100, 1);

    }

}