package mess.Algorithm;

import org.junit.Test;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

import java.nio.file.Path;
import java.nio.file.Paths;

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

        NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="4";  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(data);

        Instances newData= Filter.useFilter(data, convert);

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


    }

    @Test
    public void trainCV() throws Exception {
        System.out.println("hi jess");
    }

}